# Spring Boot 网上购物商城系统

这是一个基于 Spring Boot 的简化网上购物商城示例，包含以下功能：

- 用户注册
- 用户登录（基于 Session）
- 商品展示（静态页面：`/products.html`）
- 购物车功能（加入购物车、查看购物车）
- 订单功能（提交订单、查看订单、支付订单、退款订单）

## 技术栈

- Java 17
- Spring Boot 3
- Spring Web
- Maven

## 运行方式

```bash
mvn spring-boot:run
```

启动后访问：

- 首页：`http://localhost:8080/`
- 商品展示静态页：`http://localhost:8080/products.html`

## 主要接口

### 认证

- `POST /api/auth/register` 注册
- `POST /api/auth/login` 登录

请求体示例：

```json
{
  "username": "test",
  "password": "123456"
}
```

### 商品

- `GET /api/products` 获取商品列表

### 购物车

- `POST /api/cart/add` 加入购物车
- `GET /api/cart` 查看购物车

加入购物车请求体示例：

```json
{
  "productId": 1,
  "quantity": 2
}
```

### 订单

- `POST /api/orders` 提交订单（订单进入待付款状态并扣减库存）
- `POST /api/orders/{orderId}/pay` 支付订单
- `POST /api/orders/{orderId}/refund` 退款订单（回退库存）
- `GET /api/orders` 查看当前登录用户订单

## 测试与构建失败（403）如何修复

如果你遇到：

- `mvn test` 失败（下载依赖 403）
- `mvn spring-boot:run` 无法启动（同样是依赖下载失败）

本项目已内置 `.mvn/settings.xml` 与 `.mvn/maven.config`，默认会使用镜像仓库。你可以按以下方式修复：

### 1) 直接执行（使用默认镜像）

```bash
mvn test
mvn spring-boot:run
```

### 2) 改为你可访问的仓库（推荐公司内网 Nexus/Artifactory）

编辑 `.mvn/settings.xml` 中的 `<url>`，改成你的仓库地址，例如：

```xml
<url>http://nexus.company.com/repository/maven-public</url>
```

然后再执行：

```bash
mvn test
mvn spring-boot:run
```

### 3) 如果仍是 403/网络不可达

请让网络/运维放通你配置的 Maven 仓库地址，或提供内网可访问仓库地址。

## 说明

- 项目使用内存数据存储（示例用途），重启后数据会丢失。
- 生产环境建议接入数据库并实现密码加密、权限控制等能力。


## Apifox 导入

已提供可直接导入的 OpenAPI 文件：

- `docs/apifox-openapi.json`

在 Apifox 中选择 **导入 -> OpenAPI/Swagger**，上传该文件即可生成全部接口（含注册、登录、商品、购物车、订单、支付、退款）。
