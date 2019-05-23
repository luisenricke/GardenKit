package com.desarollo.luisvillalobos.gardenkit.Model

class PaswordReset {
    var id: Int = 0
    var token: String? = null
    var used: Boolean = false
    var created_at: String? = null
    var expired_at: String? = null
    var idUser: Int = 0

    constructor()

    constructor(token: String?, used: Boolean, created_at: String?, expired_at: String?) {
        this.token = token
        this.used = used
        this.created_at = created_at
        this.expired_at = expired_at
    }

    constructor(token: String?, used: Boolean, created_at: String?, expired_at: String?, idUser: Int) {
        this.token = token
        this.used = used
        this.created_at = created_at
        this.expired_at = expired_at
        this.idUser = idUser
    }

    constructor(id: Int, token: String?, used: Boolean, created_at: String?, expired_at: String?) {
        this.id = id
        this.token = token
        this.used = used
        this.created_at = created_at
        this.expired_at = expired_at
    }

    constructor(id: Int, token: String?, used: Boolean, created_at: String?, expired_at: String?, idUser: Int) {
        this.id = id
        this.token = token
        this.used = used
        this.created_at = created_at
        this.expired_at = expired_at
        this.idUser = idUser
    }

    companion object {
        const val TABLE_NAME = "PasswordReset"

        const val COLUMN_ID = "_id"
        const val COLUMN_TOKEN = "token"
        const val COLUMN_USED = "used"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_EXPIRED_AT = "expired_at"
        const val COLUMN_IDUSER = "idUSer"

        var TIME_EXPIRED: Int = 2

        val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TOKEN + " VARCHAR(20) NOT NULL, " +
                COLUMN_USED + " BOOLEAN NOT NULL DEFAULT(FALSE), " +
                COLUMN_CREATED_AT + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_EXPIRED_AT + " DATETIME NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP,'$TIME_EXPIRED')), " +
                COLUMN_IDUSER + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_IDUSER + ") REFERENCES " + User.TABLE_NAME + "(" + User.COLUMN_ID + ")" +
                ")"
                )


    }


}