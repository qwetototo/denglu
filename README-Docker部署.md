# 登录功能 Docker 部署说明

## 项目组成

- 后端：Spring Boot 3.2（Java 17），容器端口 `18081`
- 前端：Vue3 + Vite，构建后由 Nginx 托管，容器端口 `80`，宿主机端口 `18080`
- 数据库：MySQL 8.0，端口 `3306`，库名 `mydb`

## 目录结构

```
denglu/
├── docker-compose.yml          # 编排三个服务
├── db/
│   └── init.sql                # 首次启动自动建库表 + 测试账号
├── 后端登录/
│   └── untitled/
│       ├── Dockerfile          # 后端镜像（Maven 构建 -> JRE 运行）
│       └── .dockerignore
└── qianduanvue3/
    ├── Dockerfile              # 前端镜像（Node 构建 -> Nginx 托管）
    ├── nginx.conf              # Nginx 反向代理 /api 到后端
    └── .dockerignore
```

## 上传到云服务器

把整个 `denglu` 目录上传到服务器（例如 `scp -r denglu root@服务器IP:/opt/`）。

## 启动

```bash
cd /opt/denglu
docker compose up -d --build
```

查看运行状态：

```bash
docker compose ps
docker compose logs -f backend
```

## 访问

- 前端页面：`http://服务器IP:18080/`
- 后端接口测试：`http://服务器IP:18081/api/auth/hello`
- 测试账号：`admin` / `123456`（在 `db/init.sql` 中）

## 502 Bad Gateway 排查

页面能打开，但登录时浏览器控制台出现 `502 Bad Gateway`，通常说明前端 Nginx
没有成功连到后端容器。按下面顺序检查：

```bash
# 1. 查看三个容器是否都在运行
docker compose ps

# 2. 看后端是否启动失败，常见原因是数据库连接或 Java 异常
docker compose logs --tail=200 backend

# 3. 在服务器本机直接测试后端
curl http://127.0.0.1:18081/api/auth/hello

# 4. 在前端容器里测试能否访问后端服务名
docker compose exec frontend wget -qO- http://backend:18081/api/auth/hello
```

第 3 步如果失败，问题在后端容器；第 3 步成功但第 4 步失败，问题在 Docker
网络或服务名解析；第 4 步成功但页面仍 502，重建前端镜像：

```bash
docker compose up -d --build frontend
```

## 常用命令

```bash
# 停止
docker compose down

# 停止并删除数据卷（会清空数据库数据，慎用）
docker compose down -v

# 重新构建单个服务
docker compose up -d --build backend
```

## 修改配置

- 数据库账号密码：改 `docker-compose.yml` 中 `mysql` 和 `backend` 的
  `MYSQL_ROOT_PASSWORD` / `DB_PASSWORD`，两者保持一致。
- 对外端口：改 `docker-compose.yml` 的 `ports` 映射（例如 `"80:80"` 把前端换到公网 80）。
- 后端数据库连接：已改为读取环境变量 `DB_HOST`、`DB_PORT`、`DB_NAME`、
  `DB_USERNAME`、`DB_PASSWORD`，本地开发不设时仍走 `localhost:3306/mydb`。

## 注意事项

- 云服务器安全组需放行前端端口 `18080`；如果要从公网直接测试后端，再临时放行 `18081`，`3306` 建议不要对公网开放。
- 首次启动时 `init.sql` 只在数据卷为空时执行；若要重新初始化，先 `docker compose down -v`。
