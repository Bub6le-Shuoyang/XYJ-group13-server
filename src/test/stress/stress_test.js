/**
 * ========================================
 *  XYJ-server 抗压测试（压力测试）脚本
 * ========================================
 *  用法:
 *    node stress_test.js              → 默认配置运行
 *    node stress_test.js quick        → 快速模式（低并发）
 *    node stress_test.js full         → 完整模式（递增并发）
 *    node stress_test.js single       → 单端点测试
 * ========================================
 */

const fs = require('fs');
const path = require('path');

// ==================== 配置区 ====================

const BASE_URL = process.env.BASE_URL || 'http://localhost:7022/api/v1';

// 压测模式配置
const PROFILES = {
  quick: {
    concurrency: 5,       // 并发数
    iterations: 20,       // 每个并发用户的请求次数
    rampUpMs: 100,        // 启动间隔
    timeoutMs: 10000,     // 请求超时
  },
  medium: {
    concurrency: 20,
    iterations: 50,
    rampUpMs: 50,
    timeoutMs: 10000,
  },
  full: {
    concurrency: [5, 10, 20, 50],  // 阶梯式递增
    iterations: [30, 30, 30, 30],
    rampUpMs: 50,
    timeoutMs: 10000,
  },
  single: {
    concurrency: 1,
    iterations: 5,
    rampUpMs: 0,
    timeoutMs: 10000,
  },
};

// 不依赖数据库真实数据的公开接口（纯压测用）
const PUBLIC_ENDPOINTS = [
  { name: '获取附近驿站',  method: 'GET',  path: '/stations/nearby?lat=30.5&lng=114.3' },
  { name: '获取驿站详情',  method: 'GET',  path: '/stations/1' },
  { name: '获取乡镇资讯',  method: 'GET',  path: '/content/news' },
  { name: '获取开屏广告',  method: 'GET',  path: '/sys/ads/splash' },
  { name: 'Swagger文档',   method: 'GET',  path: '/../v3/api-docs' },  // 相对 BASE_URL
];

// 需要登录 Token 的接口（先登录拿 token）
const AUTH_ENDPOINTS = [
  { name: '获取个人信息',     method: 'GET',  path: '/auth/me' },
  { name: '获取帮助中心',     method: 'GET',  path: '/user/help-center' },
  { name: '获取客服信息',     method: 'GET',  path: '/user/customer-service' },
  { name: '获取常用地址',     method: 'GET',  path: '/user/addresses' },
  { name: '获取优惠券列表',   method: 'GET',  path: '/user/coupons?status=AVAILABLE' },
  { name: '获取钱包流水',     method: 'GET',  path: '/user/wallet/transactions' },
  { name: '获取积分商城',     method: 'GET',  path: '/user/mall/items' },
  { name: '获取兑换记录',     method: 'GET',  path: '/user/mall/redeem-records' },
  { name: '获取用户收件列表', method: 'GET',  path: '/user/packages?type=RECEIVE&page=1&size=10' },
];

// 登录凭证
const LOGIN_PAYLOAD = {
  account: 'admin@example.com',
  password: 'MyPass123!',
  role: 'ADMIN',
};

// ==================== 核心逻辑 ====================

class StressTester {
  constructor(config) {
    this.config = config;
    this.stats = {};           // 每个端点的统计
    this.globalStats = {
      totalRequests: 0,
      totalSuccess: 0,
      totalFail: 0,
      totalTime: 0,
      startTime: 0,
      endTime: 0,
      allLatencies: [],        // 所有请求延迟
    };
    this.authToken = '';
    this.tokenExpireTime = 0;
  }

  // 获取 Token（带缓存，提前30秒刷新）
  async ensureToken() {
    const now = Date.now();
    if (this.authToken && now < this.tokenExpireTime - 30000) {
      return this.authToken;
    }
    try {
      const url = `${BASE_URL}/auth/login`;
      const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(LOGIN_PAYLOAD),
        signal: AbortSignal.timeout(this.config.timeoutMs),
      });
      const json = await res.json();
      if (json && json.data && json.data.token) {
        this.authToken = json.data.token;
        this.tokenExpireTime = now + 60 * 60 * 1000; // 假设1小时有效期
        console.log(`  [INFO] Token 获取成功`);
      } else {
        console.log(`  [WARN] Token 获取失败: ${JSON.stringify(json).substring(0, 80)}`);
      }
    } catch (e) {
      console.log(`  [WARN] Token 获取异常: ${e.message}`);
    }
    return this.authToken;
  }

  // 发起单个请求
  async sendRequest(endpoint, label = '') {
    const fullLabel = label ? `${endpoint.name}${label}` : endpoint.name;
    const url = endpoint.path.startsWith('/../')
      ? `${BASE_URL.replace('/api/v1', '')}${endpoint.path.replace('/../', '/')}`
      : `${BASE_URL}${endpoint.path}`;

    const headers = {};
    if (this.authToken) {
      headers['Authorization'] = `Bearer ${this.authToken}`;
    }
    if (endpoint.method !== 'GET') {
      headers['Content-Type'] = 'application/json';
    }

    const start = Date.now();
    let success = false;
    let status = 0;
    let error = null;

    try {
      const fetchOptions = {
        method: endpoint.method,
        headers,
        signal: AbortSignal.timeout(this.config.timeoutMs),
      };
      if (endpoint.body) {
        fetchOptions.body = JSON.stringify(endpoint.body);
      }
      const res = await fetch(url, fetchOptions);
      status = res.status;
      const text = await res.text();
      let json = null;
      try { json = JSON.parse(text); } catch (_) { /* ok */ }
      success = res.ok && (!json || json.code === undefined || json.code === 200);
    } catch (e) {
      error = e.message;
    }

    const latency = Date.now() - start;

    // 更新端点统计
    if (!this.stats[endpoint.name]) {
      this.stats[endpoint.name] = {
        total: 0, success: 0, fail: 0, latencies: [],
        minLatency: Infinity, maxLatency: 0, statusCodes: {},
        errors: {},
      };
    }
    const s = this.stats[endpoint.name];
    s.total++;
    s.latencies.push(latency);
    if (latency < s.minLatency) s.minLatency = latency;
    if (latency > s.maxLatency) s.maxLatency = latency;
    if (success) { s.success++; } else { s.fail++; }
    const codeKey = `${status}${error ? ' ' + error : ''}`;
    s.statusCodes[codeKey] = (s.statusCodes[codeKey] || 0) + 1;
    if (!success) {
      s.errors[error || `HTTP ${status}`] = (s.errors[error || `HTTP ${status}`] || 0) + 1;
    }

    // 更新全局统计
    this.globalStats.totalRequests++;
    if (success) this.globalStats.totalSuccess++;
    else this.globalStats.totalFail++;
    this.globalStats.allLatencies.push(latency);

    return { success, latency, status, error };
  }

  // 并发执行
  async runConcurrent(endpoints, concurrency, iterations, label) {
    console.log(`\n${'='.repeat(60)}`);
    console.log(`  压测阶段: ${label}`);
    console.log(`  并发数: ${concurrency}  |  每用户请求数: ${iterations}  |  接口数: ${endpoints.length}`);
    console.log(`${'='.repeat(60)}`);

    const queue = [];
    for (let u = 0; u < concurrency; u++) {
      for (let i = 0; i < iterations; i++) {
        const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
        queue.push({ endpoint, label: ` [U${u + 1}]` });
      }
    }

    // 进度输出
    let completed = 0;
    let lastLog = Date.now();

    const startTime = Date.now();
    const tasks = queue.map((item, idx) =>
      new Promise(resolve => {
        const delay = Math.floor(idx / concurrency) * this.config.rampUpMs || 0;
        setTimeout(async () => {
          const result = await this.sendRequest(item.endpoint, item.label);
          completed++;
          // 每秒输出一次进度
          const now = Date.now();
          if (now - lastLog >= 1000 || completed === queue.length) {
            lastLog = now;
            const elapsed = ((now - startTime) / 1000).toFixed(1);
            const rps = (completed / (elapsed || 0.1)).toFixed(1);
            process.stdout.write(`\r  进度: ${completed}/${queue.length}  |  耗时: ${elapsed}s  |  RPS: ${rps}  `);
          }
          resolve(result);
        }, delay);
      })
    );

    await Promise.all(tasks);
    console.log('');
    return (Date.now() - startTime) / 1000;
  }

  // 计算百分位延迟
  percentile(arr, p) {
    if (!arr || arr.length === 0) return 0;
    const sorted = [...arr].sort((a, b) => a - b);
    const idx = Math.ceil((p / 100) * sorted.length) - 1;
    return sorted[Math.max(0, idx)];
  }

  // 生成报告
  generateReport(totalDuration, outputPath) {
    const gs = this.globalStats;
    const all = gs.allLatencies;
    const totalSec = totalDuration || (gs.endTime - gs.startTime) / 1000;
    const avgLatency = all.length > 0 ? (all.reduce((a, b) => a + b, 0) / all.length).toFixed(2) : 0;

    const lines = [];
    const hr = '─'.repeat(66);

    lines.push('');
    lines.push('╔' + '═'.repeat(64) + '╗');
    lines.push('║' + '   XYJ-server 抗压测试报告'.padStart(46).padEnd(64) + '║');
    lines.push('╠' + '═'.repeat(64) + '╣');
    lines.push(`║  ${('测试时间: ' + new Date().toLocaleString()).padEnd(62)}║`);
    lines.push(`║  ${('目标服务: ' + BASE_URL).padEnd(62)}║`);
    lines.push(`║  ${('测试模式: ' + (this.config.mode || 'custom')).padEnd(62)}║`);
    lines.push('╚' + '═'.repeat(64) + '╝');
    lines.push('');

    // 全局摘要
    lines.push(hr);
    lines.push('  📊  全局摘要');
    lines.push(hr);
    lines.push(`  总请求数:      ${gs.totalRequests}`);
    lines.push(`  成功:          ${gs.totalSuccess}  (${((gs.totalSuccess / gs.totalRequests) * 100).toFixed(2)}%)`);
    lines.push(`  失败:          ${gs.totalFail}  (${((gs.totalFail / gs.totalRequests) * 100).toFixed(2)}%)`);
    lines.push(`  总耗时:        ${totalSec.toFixed(2)}s`);
    lines.push(`  吞吐量 (RPS):  ${(gs.totalRequests / (totalSec || 1)).toFixed(2)} req/s`);
    lines.push(`  平均延迟:      ${avgLatency}ms`);
    lines.push(`  P50 延迟:      ${this.percentile(all, 50)}ms`);
    lines.push(`  P90 延迟:      ${this.percentile(all, 90)}ms`);
    lines.push(`  P95 延迟:      ${this.percentile(all, 95)}ms`);
    lines.push(`  P99 延迟:      ${this.percentile(all, 99)}ms`);
    lines.push(`  最小延迟:      ${all.length > 0 ? Math.min(...all) : 0}ms`);
    lines.push(`  最大延迟:      ${all.length > 0 ? Math.max(...all) : 0}ms`);
    lines.push('');

    // 分端点统计
    lines.push(hr);
    lines.push('  📍  各接口统计');
    lines.push(hr);

    const endpointEntries = Object.entries(this.stats)
      .sort((a, b) => b[1].total - a[1].total);

    for (const [name, stat] of endpointEntries) {
      const successRate = ((stat.success / stat.total) * 100).toFixed(1);
      const avg = (stat.latencies.reduce((a, b) => a + b, 0) / stat.latencies.length).toFixed(1);
      const p95 = this.percentile(stat.latencies, 95);

      // 根据成功率决定图标
      const icon = successRate >= 99 ? '✅' : successRate >= 90 ? '⚠️' : '❌';

      lines.push(`  ${icon} ${name}`);
      lines.push(`     请求: ${stat.total}  |  成功: ${stat.success}  |  失败: ${stat.fail}  |  成功率: ${successRate}%`);
      lines.push(`     平均: ${avg}ms  |  P95: ${p95}ms  |  最小: ${stat.minLatency}ms  |  最大: ${stat.maxLatency}ms`);

      if (Object.keys(stat.errors).length > 0) {
        const errSummary = Object.entries(stat.errors)
          .map(([k, v]) => `[${v}次] ${k.substring(0, 50)}`)
          .join(', ');
        lines.push(`     错误: ${errSummary}`);
      }
      lines.push('');
    }

    // 吞吐量曲线（简化版，按端点显示）
    lines.push(hr);
    lines.push('  📈  吞吐量排名（按成功请求数）');
    lines.push(hr);
    const throughputRank = endpointEntries
      .sort((a, b) => b[1].success - a[1].success)
      .slice(0, 10);
    const maxSuccess = throughputRank[0]?.[1]?.success || 1;
    for (const [name, stat] of throughputRank) {
      const barLen = Math.max(1, Math.round((stat.success / maxSuccess) * 30));
      const bar = '█'.repeat(barLen);
      lines.push(`  ${name.padEnd(20)} ${bar} ${stat.success}`);
    }
    lines.push('');

    // 延迟分布
    lines.push(hr);
    lines.push('  ⏱️  延迟分布（全部请求）');
    lines.push(hr);
    const buckets = [
      { label: '   < 50ms  ', max: 50 },
      { label: ' 50-100ms  ', max: 100 },
      { label: '100-200ms ', max: 200 },
      { label: '200-500ms ', max: 500 },
      { label: '500ms-1s  ', max: 1000 },
      { label: ' 1s-3s     ', max: 3000 },
      { label: '  > 3s     ', max: Infinity },
    ];
    for (const bucket of buckets) {
      const count = all.filter(l => l <= bucket.max && l > (buckets.find(b => b.max === bucket.max) ? 0 : (buckets.find(b => b.max === bucket.max)?.max || 0))).length;
      // simpler approach:
    }

    // 更简单的延迟分布
    const dist = { '<50ms': 0, '50-100ms': 0, '100-200ms': 0, '200-500ms': 0, '500ms-1s': 0, '1s-3s': 0, '>3s': 0 };
    for (const l of all) {
      if (l < 50) dist['<50ms']++;
      else if (l < 100) dist['50-100ms']++;
      else if (l < 200) dist['100-200ms']++;
      else if (l < 500) dist['200-500ms']++;
      else if (l < 1000) dist['500ms-1s']++;
      else if (l < 3000) dist['1s-3s']++;
      else dist['>3s']++;
    }
    const maxDist = Math.max(1, ...Object.values(dist));
    for (const [range, count] of Object.entries(dist)) {
      const barLen = Math.round((count / maxDist) * 30);
      const bar = '▓'.repeat(barLen);
      const pct = all.length > 0 ? ((count / all.length) * 100).toFixed(1) : '0.0';
      lines.push(`  ${range.padEnd(12)} ${bar} ${String(count).padStart(5)}  ${pct}%`);
    }

    lines.push('');
    lines.push(hr);
    lines.push(`  报告保存时间: ${new Date().toLocaleString()}`);
    lines.push(hr);

    const reportText = lines.join('\n');
    console.log(reportText);

    // 保存报告
    fs.writeFileSync(outputPath, reportText, 'utf-8');
    console.log(`📄 完整报告已保存至: ${outputPath}\n`);

    // 同时保存 JSON 格式（便于程序处理）
    const jsonReportPath = outputPath.replace('.txt', '.json');
    const jsonReport = {
      timestamp: new Date().toISOString(),
      baseUrl: BASE_URL,
      mode: this.config.mode,
      global: {
        totalRequests: gs.totalRequests,
        totalSuccess: gs.totalSuccess,
        totalFail: gs.totalFail,
        totalDurationSec: totalSec.toFixed(2),
        throughputRPS: (gs.totalRequests / (totalSec || 1)).toFixed(2),
        avgLatencyMs: avgLatency,
        p50Ms: this.percentile(all, 50),
        p90Ms: this.percentile(all, 90),
        p95Ms: this.percentile(all, 95),
        p99Ms: this.percentile(all, 99),
        minMs: all.length > 0 ? Math.min(...all) : 0,
        maxMs: all.length > 0 ? Math.max(...all) : 0,
        latencyDistribution: dist,
      },
      endpoints: Object.fromEntries(
        endpointEntries.map(([name, stat]) => [
          name,
          {
            total: stat.total,
            success: stat.success,
            fail: stat.fail,
            successRate: ((stat.success / stat.total) * 100).toFixed(1) + '%',
            avgMs: (stat.latencies.reduce((a, b) => a + b, 0) / stat.latencies.length).toFixed(1),
            p95Ms: this.percentile(stat.latencies, 95),
            minMs: stat.minLatency,
            maxMs: stat.maxLatency,
            errors: stat.errors,
          },
        ])
      ),
    };
    fs.writeFileSync(jsonReportPath, JSON.stringify(jsonReport, null, 2), 'utf-8');
    console.log(`📄 JSON 报告已保存至: ${jsonReportPath}`);
  }

  // 运行压测
  async run() {
    const modeConfig = this.config;
    const isStaged = Array.isArray(modeConfig.concurrency);

    // 确保 Token
    await this.ensureToken();

    this.globalStats.startTime = Date.now();
    let totalDuration = 0;

    // 公开端点压测
    console.log('\n🔓 阶段 1/2: 公开接口压测');
    if (isStaged) {
      for (let i = 0; i < modeConfig.concurrency.length; i++) {
        totalDuration += await this.runConcurrent(
          PUBLIC_ENDPOINTS,
          modeConfig.concurrency[i],
          modeConfig.iterations[i],
          `公开接口-阶梯${i + 1} (并发=${modeConfig.concurrency[i]})`
        );
        // 阶梯间稍作休息
        if (i < modeConfig.concurrency.length - 1) {
          console.log('  ⏸ 冷却 1 秒...');
          await new Promise(r => setTimeout(r, 1000));
        }
      }
    } else {
      totalDuration += await this.runConcurrent(
        PUBLIC_ENDPOINTS,
        modeConfig.concurrency,
        modeConfig.iterations,
        `公开接口 (并发=${modeConfig.concurrency})`
      );
    }

    // 确保 Token 仍然有效
    await this.ensureToken();

    // 鉴权端点压测
    console.log('\n🔒 阶段 2/2: 鉴权接口压测');
    if (isStaged) {
      for (let i = 0; i < modeConfig.concurrency.length; i++) {
        totalDuration += await this.runConcurrent(
          AUTH_ENDPOINTS,
          modeConfig.concurrency[i],
          modeConfig.iterations[i],
          `鉴权接口-阶梯${i + 1} (并发=${modeConfig.concurrency[i]})`
        );
        if (i < modeConfig.concurrency.length - 1) {
          console.log('  ⏸ 冷却 1 秒...');
          await new Promise(r => setTimeout(r, 1000));
        }
      }
    } else {
      totalDuration += await this.runConcurrent(
        AUTH_ENDPOINTS,
        modeConfig.concurrency,
        modeConfig.iterations,
        `鉴权接口 (并发=${modeConfig.concurrency})`
      );
    }

    this.globalStats.endTime = Date.now();

    // 生成报告
    const reportDir = path.resolve(__dirname);
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').substring(0, 19);
    const reportPath = path.join(reportDir, `stress_report_${timestamp}.txt`);
    this.generateReport(totalDuration, reportPath);
  }
}

// ==================== 入口 ====================

async function main() {
  const mode = process.argv[2] || 'quick';

  if (!PROFILES[mode]) {
    console.log('❌ 未知模式，可用选项: quick, medium, full, single');
    console.log('   用法: node stress_test.js [模式]');
    console.log('');
    console.log('   环境变量:');
    console.log('     BASE_URL   - 服务地址 (默认 http://localhost:7022/api/v1)');
    console.log('     CONCURRENCY - 自定义并发数');
    console.log('     ITERATIONS  - 自定义每用户请求数');
    process.exit(1);
  }

  const config = { ...PROFILES[mode], mode };

  // 环境变量覆盖
  if (process.env.CONCURRENCY) {
    config.concurrency = parseInt(process.env.CONCURRENCY);
  }
  if (process.env.ITERATIONS) {
    config.iterations = parseInt(process.env.ITERATIONS);
  }

  console.log('╔' + '═'.repeat(50) + '╗');
  console.log('║' + '     XYJ-server 抗压测试工具'.padStart(38).padEnd(50) + '║');
  console.log('║' + `     目标: ${BASE_URL}`.padEnd(50) + '║');
  console.log('║' + `     模式: ${mode}`.padEnd(50) + '║');

  if (Array.isArray(config.concurrency)) {
    console.log('║' + `     阶梯并发: ${config.concurrency.join(' → ')}`.padEnd(50) + '║');
  } else {
    console.log('║' + `     并发: ${config.concurrency}  |  迭代: ${config.iterations}`.padEnd(50) + '║');
  }
  console.log('╚' + '═'.repeat(50) + '╝');

  // 检查服务是否可达
  try {
    const check = await fetch(`${BASE_URL}/stations/nearby?lat=30.5&lng=114.3`, {
      signal: AbortSignal.timeout(5000),
    });
    console.log(`  服务连通性: ✅ 正常 (HTTP ${check.status})`);
  } catch (e) {
    console.log(`  服务连通性: ❌ 无法连接 (${e.message})`);
    console.log(`  请确保 Spring Boot 服务已在 7022 端口启动`);
    process.exit(1);
  }

  const tester = new StressTester(config);
  await tester.run();

  console.log('✅ 抗压测试完成！\n');
}

main().catch(e => {
  console.error('\n❌ 测试异常:', e.message);
  process.exit(1);
});
