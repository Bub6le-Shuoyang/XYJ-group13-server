# XYJ-server 抗压测试

## 环境要求

- **Node.js >= 18.0.0**（需支持原生 `fetch`）
- Spring Boot 服务已在 `localhost:7022` 启动
- MySQL 数据库已就绪

查看 Node 版本：`node -v`

## 快速开始

```powershell
# 进入测试目录
cd src/test/stress

# 快速模式（推荐先用这个试跑）
node stress_test.js quick

# 中等压力
node stress_test.js medium

# 完整阶梯式压测（从 5 并发逐步升到 50）
node stress_test.js full

# 单端点测试
node stress_test.js single
```

或使用 npm scripts：

```powershell
npm run quick
npm run medium
npm run full
npm run single
```

## 自定义并发参数

```powershell
# 环境变量方式
$env:CONCURRENCY=30; $env:ITERATIONS=100; node stress_test.js quick

# 一行方式（PowerShell）
CONCURRENCY=30 ITERATIONS=100 node stress_test.js quick
```

## 压测模式说明

| 模式 | 并发数 | 每用户请求 | 总请求量（约） | 适用场景 |
|------|--------|-----------|--------------|---------|
| `single` | 1 | 5 | ~70 | 冒烟测试 |
| `quick` | 5 | 20 | ~1400 | 快速验证 |
| `medium` | 20 | 50 | ~14000 | 日常压测 |
| `full` | 5→10→20→50 阶梯 | 30 | ~10200 | 找性能瓶颈 |

## 测试范围

### 公开接口（无需 Token）
- 获取附近驿站
- 获取驿站详情
- 获取乡镇资讯
- 获取开屏广告
- Swagger API 文档

### 鉴权接口（需 Bearer Token）
- 获取个人信息
- 获取帮助中心
- 获取客服信息
- 获取常用地址
- 获取优惠券列表
- 获取钱包流水
- 获取积分商城
- 获取兑换记录
- 获取用户收件列表

## 报告输出

每次测试完成后会在当前目录生成两个文件：

- `stress_report_YYYY-MM-DDTHH-mm-ss.txt` — 可读文本报告
- `stress_report_YYYY-MM-DDTHH-mm-ss.json` — JSON 结构化报告

报告包含：
- 全局吞吐量（RPS）、平均/P50/P90/P95/P99 延迟
- 每个接口的请求数、成功率、延迟分布
- 错误分类统计
- 延迟时间分布直方图

## 目标服务地址

默认测试 `http://localhost:7022/api/v1`，可通过环境变量修改：

```powershell
$env:BASE_URL="http://your-server:port/api/v1"; node stress_test.js quick
```
