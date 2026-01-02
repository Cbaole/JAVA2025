# 权限管理平台（Spring Boot + Vue3）

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+

## 启动后端（推荐本地 H2）

在项目根目录下进入 `backend`，启动：

```bash
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

后端默认地址：`http://localhost:8080`

### 默认管理员账号

启动时会自动初始化：

- 用户名：admin
- 密码：123456

## 启动前端

在项目根目录下进入 `frontend`，安装依赖并启动：

```bash
cd frontend
npm install
npm run dev -- --port 5173
```

前端地址：`http://localhost:5173`

## 常用接口验证

### 获取公共选项

`groupKey` 必填，例如：

```bash
curl "http://localhost:8080/api/public/options?groupKey=POST"
```

### 登录

```bash
curl -X POST "http://localhost:8080/api/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"123456\"}"
```

## 生产环境（MySQL）

默认配置使用 MySQL（见 `backend/src/main/resources/application.yml`），生产环境建议通过环境变量提供：

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

