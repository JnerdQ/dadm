package com.dadm.empresa.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "CompaniesDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "companies"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_WEBSITE = "website"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PRODUCTS = "products"
        private const val COLUMN_CLASSIFICATION = "classification"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_WEBSITE TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PRODUCTS TEXT,
                $COLUMN_CLASSIFICATION TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertCompany(company: SoftwareCompany): Long {
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_WEBSITE, company.website)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_PRODUCTS, company.products)
            put(COLUMN_CLASSIFICATION, company.classification)
        }
        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun getAllCompanies(): List<SoftwareCompany> {
        val companies = mutableListOf<SoftwareCompany>()
        val cursor = readableDatabase.query(TABLE_NAME, null, null, null, null, null, "$COLUMN_NAME ASC")

        with(cursor) {
            while (moveToNext()) {
                companies.add(
                    SoftwareCompany(
                        id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                        name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                        website = getString(getColumnIndexOrThrow(COLUMN_WEBSITE)),
                        phone = getString(getColumnIndexOrThrow(COLUMN_PHONE)),
                        email = getString(getColumnIndexOrThrow(COLUMN_EMAIL)),
                        products = getString(getColumnIndexOrThrow(COLUMN_PRODUCTS)),
                        classification = getString(getColumnIndexOrThrow(COLUMN_CLASSIFICATION))
                    )
                )
            }
        }
        cursor.close()
        return companies
    }

    fun updateCompany(company: SoftwareCompany): Int {
        val values = ContentValues().apply {
            put(COLUMN_NAME, company.name)
            put(COLUMN_WEBSITE, company.website)
            put(COLUMN_PHONE, company.phone)
            put(COLUMN_EMAIL, company.email)
            put(COLUMN_PRODUCTS, company.products)
            put(COLUMN_CLASSIFICATION, company.classification)
        }
        return writableDatabase.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(company.id.toString()))
    }

    fun deleteCompany(id: Int): Int {
        return writableDatabase.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getFilteredCompanies(nameFilter: String, classificationFilter: String): List<SoftwareCompany> {
        val selection = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        if (nameFilter.isNotEmpty()) {
            selection.add("$COLUMN_NAME LIKE ?")
            selectionArgs.add("%$nameFilter%")
        }
        if (classificationFilter.isNotEmpty()) {
            selection.add("$COLUMN_CLASSIFICATION LIKE ?")
            selectionArgs.add("%$classificationFilter%")
        }

        val finalSelection = if (selection.isEmpty()) null else selection.joinToString(" AND ")
        val finalSelectionArgs = if (selectionArgs.isEmpty()) null else selectionArgs.toTypedArray()

        val cursor = readableDatabase.query(
            TABLE_NAME,
            null,
            finalSelection,
            finalSelectionArgs,
            null,
            null,
            "$COLUMN_NAME ASC"
        )

        val companies = mutableListOf<SoftwareCompany>()
        with(cursor) {
            while (moveToNext()) {
                companies.add(
                    SoftwareCompany(
                        id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                        name = getString(getColumnIndexOrThrow(COLUMN_NAME)),
                        website = getString(getColumnIndexOrThrow(COLUMN_WEBSITE)),
                        phone = getString(getColumnIndexOrThrow(COLUMN_PHONE)),
                        email = getString(getColumnIndexOrThrow(COLUMN_EMAIL)),
                        products = getString(getColumnIndexOrThrow(COLUMN_PRODUCTS)),
                        classification = getString(getColumnIndexOrThrow(COLUMN_CLASSIFICATION))
                    )
                )
            }
        }
        cursor.close()
        return companies
    }
}