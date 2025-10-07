# Android Starter Kit with Appwrite

Kickstart your Android development with this ready-to-use starter project integrated
with [Appwrite](https://appwrite.io).

This guide will help you quickly set up, customize, and build your Android app using **Jetpack
Compose**.

---

## 🚀 Getting Started

### Clone the Project

Clone this repository to your local machine using Git or directly from `Android Studio`:

```bash
git clone https://github.com/appwrite/starter-for-android
```

Alternatively, open the repository URL in `Android Studio` to clone it directly.

---

## 🛠️ Development Guide

1. **Configure Appwrite**  
   Navigate to `constants/AppwriteConfig.kt` and update the values to match your Appwrite project
   credentials.

2. **Customize as Needed**  
   Modify the starter kit to suit your app's requirements. Adjust UI, features, or backend
   integrations as per your needs.

3. **Run the App**  
   Select a target device (emulator or connected physical Android device) in `Android Studio`, and
   click **Run** to start the app.

---

## 📦 Building for Production

To create a production build of your app:

1. Open **Build variants** > **app** in the menu bar.
2. Choose **release**
3. Build and deploy your app in release mode.

---

## 💡 Additional Notes

- This starter project is designed to streamline your Android development with Appwrite.
- Refer to the [Appwrite Documentation](https://appwrite.io/docs) for detailed integration guidance.

---

## EcoPulse - Market Trends Analyzer 📈

A prototype native Android application that provides users with a clean, immediate, and insightful overview of key financial market indicators across stocks, metals, and cryptocurrencies. The app demonstrates a comprehensive understanding of modern Android development, including REST API integration, data visualization (candlestick charts), asynchronous programming with Kotlin Coroutines, and Backend-as-a-Service (BaaS) for user management.

## 💡 Project Vision

In a volatile economic landscape, retail investors and curious individuals need a simple tool to get a "pulse" of the market without being overwhelmed by complex trading platforms. EcoPulse aims to solve this by aggregating data from diverse asset classes—traditional stocks, safe-haven metals, and volatile cryptocurrencies—into a single, easy-to-digest mobile experience.

## ✨ Core Features

The application is built around distinct, user-centric features, focusing on data aggregation, analysis, and visualization.

1. Secure User Authentication
Robust sign-up and sign-in functionality powered by Appwrite BaaS.

Ensures user data is secure and provides a persistent session.

2. REST API Integration
Successfully integrates three distinct external REST APIs using Retrofit for clean, type-safe network calls:

Alpha Vantage: For fetching historical daily stock data (SPY).

Binance: For fetching historical daily cryptocurrency data (BTC/USDT).

MetalPriceAPI: For fetching current precious metals prices (Gold/XAU).

3. Interactive Data Visualization
A dedicated "Trends" screen displays market data on an interactive candlestick chart powered by the MPAndroidChart library.

Users can scroll, pinch-to-zoom, and inspect historical data points.

A ChipGroup allows dynamic switching between data sources (Stocks, Gold, Bitcoin).

4. Predictive Forecasting Model (Proof-of-Concept)
Implements a client-side 7-day price forecast using a simple linear regression algorithm.

The forecast is calculated based on the 30-day historical closing prices and overlaid on the chart as a dashed line.

This feature is toggleable via a checkbox.

5. Synthesized Economic Indicator
The home screen features a dynamic "Recession Score", a custom algorithm that synthesizes the 30-day performance of all three tracked assets.

Provides an at-a-glance, opinionated market sentiment indicator ("Stable," "Caution," "Risky"), demonstrating complex data processing.

## 🏗️ Architectural Design & Technology Stack

The project is built using modern Android development principles and industry-standard tools.

Component	Technology/Library	Justification
Language	Kotlin	Official language for Android development; provides null safety, concise syntax, and coroutine support.
Asynchronous	Kotlin Coroutines	Manages background threads for network calls efficiently, preventing UI freezes and simplifying async logic.
Networking	Retrofit & Gson	Industry standard for building type-safe REST clients and simplifying JSON parsing.
Backend-as-a-Service	Appwrite	Provides a ready-made, secure backend for user authentication, abstracting server-side development.
Data Visualization	MPAndroidChart	Powerful, highly customizable charting library for financial applications.
UI Components	Android XML, Material Design	Ensures a modern, dark-theme-first aesthetic consistent with the platform's design language.
View Access	View Binding	Provides null-safe and type-safe access to views, eliminating findViewById.

## 🚀 Setup and Installation Guide

Follow these steps to build and run the project locally.

1. Prerequisites
Android Studio Hedgehog (2023.1.1) or newer.

An Android device or emulator running API level 26 or higher.

Active API keys for Alpha Vantage.

A configured Appwrite project instance.

2. Clone the Repository
Bash

git clone https://github.com/ManieDP/EcoPulse_-master.git
cd EcoPulse_-master
3. Configure Credentials
Create a file named local.properties in the project's root directory. This file is included in .gitignore and must not be committed to source control.

Properties

# API Keys for Data Services
ALPHAVANTAGE_API_KEY=YOUR_ALPHA_VANTAGE_API_KEY
METALPRICE_API_KEY=YOUR_METALPRICE_API_KEY

# Appwrite Backend Configuration
APPWRITE_ENDPOINT=https://your.appwrite.instance/v1
APPWRITE_PROJECT_ID=YOUR_APPWRITE_PROJECT_ID
4. Build and Run
Open the project in Android Studio.

Gradle will automatically sync.

Select a target device (emulator or physical device).

Click the Run 'app' button (▶️).

Note: The build.gradle.kts file is configured to read the properties from local.properties and make them available in the app via the BuildConfig class.

## ✅ Automated Testing (Future Work)
To achieve the "Greatly exceeds the required standard" criteria, implementing automated tests is the next logical step.

Type of Test	Candidate for Testing	Action/Goal
Unit Tests	generateForecast() and calculateRecessionScore() functions.	Use JUnit to test these pure, deterministic functions with known inputs and assert expected outputs.
Integration Tests	API parsing logic.	Use MockWebServer (from OkHttp) to provide fake JSON responses and verify that Retrofit services and data classes parse them correctly.
UI Tests	User flows, e.g., "tap BTC chip, then check the forecast checkbox."	Use Espresso to simulate user interactions and assert that the chart UI updates as expected.


## ❗ Known Issues & Limitations
Gold (XAU) Data is Mocked: Due to the lack of a free and reliable REST API providing daily OHLC historical data for precious metals, the Gold chart currently uses randomly generated mock data. This serves as a placeholder for a future real API integration.

Forecast Model is Simplistic: The 7-day forecast uses a basic linear regression model. It should be treated as a proof-of-concept demonstration of analytical capability and not as financial advice.

API Rate Limiting: The free Alpha Vantage API key has strict rate limits. Frequent app restarts or data fetches may result in temporary API errors.
