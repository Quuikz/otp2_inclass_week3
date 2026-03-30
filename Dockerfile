FROM maven:latest

LABEL authors="riku"

ENV DISPLAY=host.docker.internal:0

WORKDIR /app

COPY . .

RUN apt-get update && \
apt-get install -y \
fonts-liberation \
fonts-dejavu-core \
fonts-noto-core \
fonts-noto-cjk \
fontconfig \
libfreetype6 \
locales \
libgtk-3-0 \
libglu1-mesa \
libxtst6 \
libxrender1 \
libxi6 \
libasound2t64 \
libgbm1 \
libx11-6 \
maven \
wget \
unzip && \
&& locale-gen ja_JP.UTF-8 \
apt-get clean && rm -rf /var/lib/apt/lists/*

#rebuild font cache
RUN fc-cache -fv

RUN mvn -f pom.xml clean package -DskipTests# Download JavaFX SDK 21

RUN ARCH=$(uname -m) && \
    if [ "$ARCH" = "x86_64" ]; then \
        wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip; \
    else \
        wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-aarch64_bin-sdk.zip -O /tmp/openjfx.zip; \
    fi && \
    unzip /tmp/openjfx.zip -d /opt && \
    rm /tmp/openjfx.zip

CMD java \
    --module-path /opt/javafx-sdk-21/lib\
    --add-modules javafx.fxml,javafx.graphics,javafx.controls,javafx.base\
    -jar /target/otp2_inclass_week2-1.0-SNAPSHOT.jar
