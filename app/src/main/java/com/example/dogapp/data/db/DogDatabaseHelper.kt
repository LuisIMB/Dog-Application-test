package com.example.dogapp.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dogapp.data.model.DogItem

class DogDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "dog_breeds.db"
        const val DATABASE_VERSION = 1

        const val TABLE_BREEDS = "breeds"
        const val COLUMN_BREED_ID = "id"
        const val COLUMN_BREED_NAME = "name"

        const val TABLE_SUBBREEDS = "subbreeds"
        const val COLUMN_SUBBREED_ID = "id"
        const val COLUMN_SUBBREED_NAME = "name"
        const val COLUMN_SUBBREED_BREED_ID = "breed_id"

        const val TABLE_IMAGES = "images"
        const val COLUMN_IMAGE_ID = "id"
        const val COLUMN_IMAGE_URL = "imageUrl"
        const val COLUMN_IMAGE_BREED_ID = "breed_id"
        const val COLUMN_IMAGE_SUBBREED_ID = "subbreed_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createBreedsTable = """
            CREATE TABLE $TABLE_BREEDS (
                $COLUMN_BREED_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BREED_NAME TEXT UNIQUE
            );
        """.trimIndent()
        val createSubBreedsTable = """
            CREATE TABLE $TABLE_SUBBREEDS (
                $COLUMN_SUBBREED_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBBREED_NAME TEXT,
                $COLUMN_SUBBREED_BREED_ID INTEGER,
                UNIQUE($COLUMN_SUBBREED_NAME, $COLUMN_SUBBREED_BREED_ID),
                FOREIGN KEY($COLUMN_SUBBREED_BREED_ID) REFERENCES $TABLE_BREEDS($COLUMN_BREED_ID)
            );
        """.trimIndent()
        val createImagesTable = """
            CREATE TABLE $TABLE_IMAGES (
                $COLUMN_IMAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_IMAGE_URL TEXT UNIQUE,
                $COLUMN_IMAGE_BREED_ID INTEGER,
                $COLUMN_IMAGE_SUBBREED_ID INTEGER,
                FOREIGN KEY($COLUMN_IMAGE_BREED_ID) REFERENCES $TABLE_BREEDS($COLUMN_BREED_ID),
                FOREIGN KEY($COLUMN_IMAGE_SUBBREED_ID) REFERENCES $TABLE_SUBBREEDS($COLUMN_SUBBREED_ID)
            );
        """.trimIndent()
        db.execSQL(createBreedsTable)
        db.execSQL(createSubBreedsTable)
        db.execSQL(createImagesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_IMAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUBBREEDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BREEDS")
        onCreate(db)
    }
    fun insertBreedWithSubBreedImages(breed: String, subBreed: String?, imageUrls: List<String>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val breedId = insertOrGetBreedId(breed, db)
            val subBreedId = subBreed?.let { insertOrGetSubBreedId(it, breedId, db) }

            imageUrls.forEach { url ->
                val values = ContentValues().apply {
                    put(COLUMN_IMAGE_URL, url)
                    put(COLUMN_IMAGE_BREED_ID, breedId)
                    subBreedId?.let { put(COLUMN_IMAGE_SUBBREED_ID, it) }
                }
                db.insert(TABLE_IMAGES, null, values)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun insertOrGetBreedId(breed: String, db: SQLiteDatabase): Long {
        val cursor = db.rawQuery(
            "SELECT $COLUMN_BREED_ID FROM $TABLE_BREEDS WHERE $COLUMN_BREED_NAME = ?",
            arrayOf(breed)
        )
        cursor.use {
            if (it.moveToFirst()) return it.getLong(0)
        }

        val values = ContentValues().apply { put(COLUMN_BREED_NAME, breed) }
        return db.insert(TABLE_BREEDS, null, values)
    }

    private fun insertOrGetSubBreedId(subBreed: String, breedId: Long, db: SQLiteDatabase): Long {
        val cursor = db.rawQuery(
            "SELECT $COLUMN_SUBBREED_ID FROM $TABLE_SUBBREEDS WHERE $COLUMN_SUBBREED_NAME = ? AND $COLUMN_SUBBREED_BREED_ID = ?",
            arrayOf(subBreed, breedId.toString())
        )
        cursor.use {
            if (it.moveToFirst()) return it.getLong(0)
        }

        val values = ContentValues().apply {
            put(COLUMN_SUBBREED_NAME, subBreed)
            put(COLUMN_SUBBREED_BREED_ID, breedId)
        }
        return db.insert(TABLE_SUBBREEDS, null, values)
    }

    fun getAllDogItems(): List<DogItem> {
        val list = mutableListOf<DogItem>()
        val db = readableDatabase
        val query = """
            SELECT b.$COLUMN_BREED_NAME, sb.$COLUMN_SUBBREED_NAME, MIN(i.$COLUMN_IMAGE_URL)
            FROM $TABLE_IMAGES i
            LEFT JOIN $TABLE_BREEDS b ON i.$COLUMN_IMAGE_BREED_ID = b.$COLUMN_BREED_ID
            LEFT JOIN $TABLE_SUBBREEDS sb ON i.$COLUMN_IMAGE_SUBBREED_ID = sb.$COLUMN_SUBBREED_ID
            GROUP BY b.$COLUMN_BREED_NAME, sb.$COLUMN_SUBBREED_NAME
            ORDER BY b.$COLUMN_BREED_NAME ASC
        """
        val cursor = db.rawQuery(query, null)
        cursor.use {
            while (it.moveToNext()) {
                val breed = it.getString(0)
                val subBreed = it.getString(1) // may be null
                val imageUrl = it.getString(2)
                list.add(DogItem(breed, subBreed, imageUrl))
            }
        }
        return list
    }

    fun getImagesForBreed(breed: String, subBreed: String?): List<String> {
        val db = readableDatabase
        val images = mutableListOf<String>()
        val query = if (subBreed != null) {
            """
                SELECT i.$COLUMN_IMAGE_URL
                FROM $TABLE_IMAGES i
                JOIN $TABLE_BREEDS b ON i.$COLUMN_IMAGE_BREED_ID = b.$COLUMN_BREED_ID
                JOIN $TABLE_SUBBREEDS sb ON i.$COLUMN_IMAGE_SUBBREED_ID = sb.$COLUMN_SUBBREED_ID
                WHERE b.$COLUMN_BREED_NAME = ? AND sb.$COLUMN_SUBBREED_NAME = ?
            """
        } else {
            """
                SELECT i.$COLUMN_IMAGE_URL
                FROM $TABLE_IMAGES i
                JOIN $TABLE_BREEDS b ON i.$COLUMN_IMAGE_BREED_ID = b.$COLUMN_BREED_ID
                WHERE b.$COLUMN_BREED_NAME = ? AND i.$COLUMN_IMAGE_SUBBREED_ID IS NULL
            """
        }

        val args = if (subBreed != null) arrayOf(breed, subBreed) else arrayOf(breed)
        val cursor = db.rawQuery(query, args)

        cursor.use {
            while (it.moveToNext()) {
                images.add(it.getString(0))
            }
        }

        return images
    }
}

