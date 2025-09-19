package io.appwrite.starterkit

import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases

// This is the global, static object that holds our initialized Appwrite services
object Appwrite {
    lateinit var client: Client
    lateinit var account: Account // This now correctly refers to io.appwrite.services.Account
    lateinit var databases: Databases
}
