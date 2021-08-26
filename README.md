# 專案說明

共用(元件)模組

* commerce-stream
* commerce-cache

## 專案執行環境

* Java 13 (jdk-13.0.2)
* apache-maven-3.6.3
* gradle-6.4

## 共用組件部屬到本機.m2

預設路徑 C:\Users\User\.m2\repository

```sh
# 專案目錄
cd ./commerce-cache \
  && mvn clean package deploy "-Dmaven.test.skip=true"

cd ./commerce-stream \
  && mvn clean package deploy "-Dmaven.test.skip=true"
```
