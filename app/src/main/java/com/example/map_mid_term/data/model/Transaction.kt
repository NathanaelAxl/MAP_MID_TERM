package com.example.map_mid_term.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.Date

data class Transaction(
    // @Exclude means this field is not stored in the Firestore document itself,
    // but we fill it manually using document.id when fetching.
    @Exclude
    var id: String = "",

    @PropertyName("userId")
    var userId: String = "",

    @PropertyName("amount")
    var amount: Double = 0.0,

    @PropertyName("type")
    var type: String = "", // "credit" or "debit" or "loan_payment"

    @PropertyName("description")
    var description: String = "",

    // Use 'date' to align with the ViewModel and Adapter.
    // Firestore stores this as a Timestamp, which maps to java.util.Date.
    @PropertyName("date")
    var date: Date? = null,

    @PropertyName("status")
    var status: String = "success",

    @PropertyName("proofImageUrl")
    var proofImageUrl: String? = null
) {
    // Required empty constructor for Firestore
    constructor() : this("", "", 0.0, "", "", null, "success", null)

    // Auxiliary constructor for dummy data in MonthlyReportFragment
    constructor(description: String, date: Date?, amount: Double, type: String) : this() {
        this.description = description
        this.date = date
        this.amount = amount
        this.type = type
    }
}