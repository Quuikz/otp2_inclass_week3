FROM maven:latest

LABEL authors="riku"

ENV DISPLAY=host.docker.internal:0

WORKDIR /app

COPY . .


#try installing japanese fonts
RUN apt-get update \
&& apt-get install -y \
fonts-liberation \
fonts-dejavu-core \
fonts-noto-core \
fonts-noto-cjk \
fontconfig \
libfreetype6 \
locales \
&& locale-gen ja_JP.UTF-8 \
&& rm -rf /var/lib/apt/lists/*

#rebuild font cache
RUN fc-cache -f

#Install dependencies for GUI + Maven build
RUN apt-get update \
    && apt-get install -y maven wget unzip libgtk-3-0 libgbm1 libx11-6 \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK 21
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip \
    && unzip /tmp/openjfx.zip -d /opt \
    && rm /tmp/openjfx.zip

RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-aarch64_bin-sdk.zip -O /tmp/openjfx.zip; \
    fi \
    && unzip /tmp/openjfx.zip -d /opt \
    && rm /tmp/openjfx.zip

RUN mvn -f pom.xml clean package -DskipTests

CMD java \
    --module-path /opt/javafx-sdk-21/lib\
    --add-modules javafx.fxml,javafx.graphics,javafx.controls,javafx.base\
    -jar /target/otp2_inclass_week2-1.0-SNAPSHOT.jar
