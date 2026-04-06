FROM maven:3.9-eclipse-temurin-21

LABEL authors="riku"
ARG TARGETARCH

# Set environment variables
ENV DISPLAY=host.docker.internal:0
ENV LANG=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8
WORKDIR .

# 2. Copy pom first to leverage Docker layer caching
COPY . .

# 3. Install Font and GUI dependencies
RUN apt-get update && apt-get install -y \
    fonts-liberation \
    fonts-dejavu-core \
    fonts-noto-core \
    fonts-noto-ui-core \
    fonts-noto-extra \
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
    && locale-gen en_US.UTF-8 ru_RU.UTF-8 fa_IR.UTF-8 ja_JP.UTF-8 \
    && fc-cache -f \
    && rm -rf /var/lib/apt/lists/*

# 4. Download JavaFX SDK that matches container architecture
RUN set -eux; \
    case "${TARGETARCH}" in \
      arm64) FX_ARCH="aarch64" ;; \
      amd64) FX_ARCH="x64" ;; \
      *) echo "Unsupported TARGETARCH: ${TARGETARCH}" >&2; exit 1 ;; \
    esac; \
    wget "https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-${FX_ARCH}_bin-sdk.zip" -O /tmp/openjfx.zip; \
    unzip /tmp/openjfx.zip -d /opt; \
    rm /tmp/openjfx.zip

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
