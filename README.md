# 使用说明

### 使用

映射目录：session 作为Telegram下载的数据，已经应用产生的数据。下载的数据存在 session/downloads 中。


可选环境变量
* `APP_ID` 客户端app id，可在 https://my.telegram.org/ 获取
* `API_HASH` 客户端 api hash， 可在 https://my.telegram.org/ 获取
* `PHONE` 登录Telegram 的手机号码

```shell
mkdir -p ./session/data
docker run --name tmd -d -e APP_ID='YOUR_APP_ID' -e API_HASH='YOUR_API_HASH' -v ./data:/home/app/data -v ./downloads:/home/app/downloads --restart=always -p 3222:3222 huangzulin/tmd
```

### 构建

构建多平台docker镜像

```shell
docker buildx create --name tmd-builder
docker buildx use tmd-builder
docker buildx build --push --platform linux/arm64,linux/amd64 -t huangzulin/tmd:latest .
```
