FROM maven:3.9-eclipse-temurin-21

LABEL authors="riku"

# Set environment variables
ENV DISPLAY=host.docker.internal:0
WORKDIR /app

# 2. Copy pom first to leverage Docker layer caching
COPY . .

# 3. Install Font and GUI dependencies
RUN apt-get update && apt-get install -y \
    fonts-liberation \
    fonts-dejavu-core \
    fonts-noto-core \
    fonts-noto-cjk \
    fontconfig \
    libfreetype6 \
    locales \
    wget \
    unzip \
    xvfb \
    libgtk-3-0 \
    libgbm1 \
    libx11-6 \
    libxrender1 \
    libxext6 \
    libxrandr2 \
    && locale-gen ja_JP.UTF-8 \
    && fc-cache -f \
    && rm -rf /var/lib/apt/lists/*

# 4. Download JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip -O /tmp/openjfx.zip \
    && unzip /tmp/openjfx.zip -d /opt \
    && rm /tmp/openjfx.zip

# 5. Build the application and collect runtime dependencies
RUN mvn -f pom.xml clean package dependency:copy-dependencies -DskipTests

# 6. Run the application (jar is not executable with -jar, so launch the main class on classpath)
CMD ["java", \
     "-Djava.awt.headless=false", \
     "-Djavafx.platform=gtk", \
     "-Dprism.order=sw", \
     "--module-path", "/opt/javafx-sdk-21/lib", \
     "--add-modules", "javafx.fxml,javafx.graphics,javafx.controls,javafx.base", \
     "-cp", "target/otp2_inclass_week3-1.0-SNAPSHOT.jar:target/dependency/*", \
     "org.otp.Launcher"]
