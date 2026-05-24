const fs = require('fs');

const BASE_URL = 'http://localhost:7022/api/v1';
let authToken = '';
let refreshToken = '';
let report = [];

async function testEndpoint(name, method, path, body = null, headers = {}, isFormData = false) {
    try {
        const url = `${BASE_URL}${path}`;
        const fetchOptions = {
            method,
            headers: {
                ...headers,
                ...(authToken ? { 'Authorization': `Bearer ${authToken}` } : {})
            }
        };

        if (body) {
            if (isFormData) {
                // simple multipart mock for test
                const boundary = '----WebKitFormBoundary7MA4YWxkTrZu0gW';
                fetchOptions.headers['Content-Type'] = `multipart/form-data; boundary=${boundary}`;
                let data = `--${boundary}\r\nContent-Disposition: form-data; name="scene"\r\n\r\nAVATAR\r\n`;
                data += `--${boundary}\r\nContent-Disposition: form-data; name="file"; filename="test.txt"\r\nContent-Type: text/plain\r\n\r\nTest content\r\n`;
                data += `--${boundary}--`;
                fetchOptions.body = data;
            } else {
                fetchOptions.headers['Content-Type'] = 'application/json';
                fetchOptions.body = JSON.stringify(body);
            }
        }

        const start = Date.now();
        const res = await fetch(url, fetchOptions);
        const text = await res.text();
        const time = Date.now() - start;

        let json = null;
        try { json = JSON.parse(text); } catch(e) {}

        const success = res.ok && json && json.code === 200;
        
        report.push({
            name,
            method,
            path,
            status: res.status,
            success,
            response: json ? (JSON.stringify(json).substring(0, 100) + '...') : text.substring(0, 50),
            time
        });

        console.log(`[${success ? 'OK' : 'FAIL'}] ${name} (${method} ${path}) - ${res.status} ${time}ms`);
        if (!success) {
            console.log(`  Response: ${text.substring(0, 200)}`);
        }

        return json;
    } catch (e) {
        report.push({ name, method, path, success: false, error: e.message });
        console.log(`[ERROR] ${name} (${method} ${path}) - ${e.message}`);
        return null;
    }
}

async function runTests() {
    console.log('Starting API Tests...\n');

    // 3. Auth
    const loginRes = await testEndpoint('Login', 'POST', '/auth/login', { account: 'admin@example.com', password: 'MyPass123!', role: 'ADMIN' });
    if (loginRes && loginRes.data) {
        authToken = loginRes.data.token;
        refreshToken = loginRes.data.refresh_token;
    } else {
        console.log('Using dummy token as fallback');
        authToken = 'dummy_token';
    }

    await testEndpoint('Get Me', 'GET', '/auth/me');
    await testEndpoint('Refresh Token', 'POST', '/auth/refresh', { refresh_token: refreshToken || 'dummy_refresh' });

    // 4. Admin Auth
    await testEndpoint('Admin Captcha', 'GET', '/admin/auth/captcha');
    await testEndpoint('Admin Send Email Code', 'POST', '/admin/auth/send-email-code', { email: 'admin@example.com' });
    await testEndpoint('Admin Register', 'POST', '/admin/auth/register', { email: 'admin@example.com', password: 'MyPass123!', code: '123' });
    await testEndpoint('Admin Login', 'POST', '/admin/auth/login', { email: 'admin@example.com', password: 'MyPass123!' });
    await testEndpoint('Admin Reset Password', 'POST', '/admin/auth/reset-password', { email: 'admin@example.com', newPassword: 'MyPass123!', code: '123' });

    // 5. Admin Profile
    await testEndpoint('Get Admin Profile', 'GET', '/admin/profile');
    await testEndpoint('Update Admin Profile', 'PUT', '/admin/profile', { realName: 'New Name', phone: '138', avatarUrl: 'img' });

    // 6. User Packages
    await testEndpoint('Get User Packages', 'GET', '/user/packages?type=RECEIVE&page=1&size=10');
    await testEndpoint('Get Package Details', 'GET', '/user/packages/PKG-001');
    await testEndpoint('Confirm Package', 'POST', '/user/packages/PKG-001/confirm');
    await testEndpoint('Rate Package', 'POST', '/user/packages/PKG-001/rate', { score: 5, comment: 'Good' });
    await testEndpoint('Complain Package', 'POST', '/user/packages/PKG-001/complain', { reason: 'Broken', description: 'Bad', images: [] });

    // 7. User Profile
    await testEndpoint('Get User Profile Home', 'GET', '/user/profile');
    await testEndpoint('Get User Addresses', 'GET', '/user/addresses');
    await testEndpoint('Add Address', 'POST', '/user/addresses', { name: 'A', phone: '1', address: 'B', is_default: false });
    await testEndpoint('Get Coupons', 'GET', '/user/coupons?status=AVAILABLE');
    await testEndpoint('Get Wallet Tx', 'GET', '/user/wallet/transactions');
    await testEndpoint('Get Mall Items', 'GET', '/user/mall/items');
    await testEndpoint('Redeem Item', 'POST', '/user/mall/items/1/redeem');
    await testEndpoint('Get Redeem Records', 'GET', '/user/mall/redeem-records');
    await testEndpoint('Get Help Center', 'GET', '/user/help-center');
    await testEndpoint('Get Customer Service', 'GET', '/user/customer-service');

    // 8. Admin Packages
    await testEndpoint('Get Station Packages', 'GET', '/admin/packages?status=IN_STOCK');
    await testEndpoint('Package Inbound', 'POST', '/admin/packages/PKG-001/inbound', { shelf_number: 'A-01' });
    await testEndpoint('Package Outbound', 'POST', '/admin/packages/PKG-001/outbound');
    await testEndpoint('Publish Task', 'POST', '/admin/tasks', { package_id: 'PKG-001', reward_amount: 8.0 });
    await testEndpoint('Get Station Stats', 'GET', '/admin/station/statistics');

    // 9. Courier Tasks
    await testEndpoint('Get Available Tasks', 'GET', '/courier/tasks/available');
    await testEndpoint('Get My Tasks', 'GET', '/courier/tasks/mine?status=ASSIGNED');
    await testEndpoint('Grab Task', 'POST', '/courier/tasks/TASK-001/grab');
    await testEndpoint('Pickup Task', 'POST', '/courier/tasks/TASK-001/pickup');
    await testEndpoint('Deliver Task', 'POST', '/courier/tasks/TASK-001/deliver', { deliver_image: 'img', remark: 'ok' });
    await testEndpoint('Verify Pickup Code', 'POST', '/courier/tasks/TASK-001/verify-pickup-code', { pickup_code: '123' });
    await testEndpoint('Get Courier Earnings', 'GET', '/courier/earnings');
    await testEndpoint('Get Courier Profile', 'GET', '/courier/profile');

    // 10. Content
    await testEndpoint('Get News', 'GET', '/content/news');
    await testEndpoint('Post News', 'POST', '/content/news', { title: 'T', content: 'C', tag: 'Tag', is_urgent: false });
    await testEndpoint('Like News', 'POST', '/content/news/1/like');
    await testEndpoint('Comment News', 'POST', '/content/news/1/comments', { content: 'Nice' });

    // 11. Stations
    await testEndpoint('Get Nearby Stations', 'GET', '/stations/nearby?lat=30.5&lng=114.3');
    await testEndpoint('Get Station Detail', 'GET', '/stations/1');

    // 12. Upload
    await testEndpoint('Upload File', 'POST', '/upload', {}, {}, true);

    // 13. Admin Users
    await testEndpoint('Get Admin Users', 'GET', '/admin/users');
    await testEndpoint('Create User', 'POST', '/admin/users', { account: 'test1', password: '123', role: 'VILLAGER' });
    await testEndpoint('Update User', 'PUT', '/admin/users/1', { account: 'test1' });
    await testEndpoint('Delete User', 'DELETE', '/admin/users/1');

    // Logout at the end
    await testEndpoint('Logout', 'POST', '/auth/logout');

    const total = report.length;
    const passed = report.filter(r => r.success).length;
    
    console.log(`\n============================`);
    console.log(`Test Summary: ${passed}/${total} passed`);
    console.log(`============================`);
    
    fs.writeFileSync('test_report.json', JSON.stringify(report, null, 2));
}

runTests();