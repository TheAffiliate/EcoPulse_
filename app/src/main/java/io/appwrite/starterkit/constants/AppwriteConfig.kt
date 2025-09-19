// 1. CRITICAL: This package declaration MUST match the folder structure.
package io.appwrite.starterkit.constants

/**
 * Appwrite integration constants.
 *
 * This object holds values related to the Appwrite server setup,
 * including version, project details, and API endpoint.
 */
object AppwriteConfig {
    /**
     * Appwrite Server version.
     */
    const val APPWRITE_VERSION = "1.6.0"

    /**
     * Appwrite project id.
     */
    const val APPWRITE_PROJECT_ID = "68c46de1003543ce190b"

    /**
     * Appwrite project name.
     */
    const val APPWRITE_PROJECT_NAME =  "EcoPulse"

    /**
     * Appwrite server endpoint url.
     */
    const val APPWRITE_PUBLIC_ENDPOINT = "https://cloud.appwrite.io/v1" // Corrected from fra.cloud... to match your previous code
}
