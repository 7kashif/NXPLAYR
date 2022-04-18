package com.nxplayr.fsl.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.nxplayr.fsl.data.model.SignupData

import com.google.gson.Gson
import com.nxplayr.fsl.data.model.LanguageLabelPojo
import com.nxplayr.fsl.data.model.LanguageList
import com.nxplayr.fsl.data.model.LanguageListData
import com.quickblox.core.helper.StringifyArrayList
import com.quickblox.users.model.QBUser

class SessionManager {
    companion object {
        val PREF_NAME: String = "LoginSession"
        val KEY_IS_LOGGEDIN: String = "isLoggedIn"
        val KEY_USER_OBJ: String = "AuthenticateUser"
        val KEY_USER_SETTINGS: String = "DefaultSettings"
        val KEY_USER_EMAIL: String = "UserEmail"
        val KEY_USER_PASSWORD: String = "Location"
        val KEY_RECENT_SEARH: String = ""
        val KEY_DynamicString: String = ""
        val KEY_IsVerify: String = "isVerify"
        val Key_Email: String = "isEmailLogin"
        val KEY_SelectedLanguage: String = "language"
        val KEY_SelectedLanguageId: String = "languageId"
        val KEY_USER_WEBS: String = "KEY_USER_WEBS"

        val Key_PushCounter: String = "pushCounter"//fireBase
        val TAG = SessionManager::class.java.simpleName

        val KEY_IS_QBUser: String = "isqbUser"
        val QB_USER_ID: String = "qbUserId"
        val QB_USER_FULL_NAME: String = "qbUserName"
        val QB_USER_LOGIN: String = "qbUserLogin"
        val QB_USER_PASSWORD: String = "qbUserPassword"
        val QB_USER_TAGS: String = "qbUserTags"
    }

    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var _context: Context
    var PRIVATE_MODE = 0

    public constructor(context: Context) {
        this._context = context
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    public fun create_login_session(
        AuthenticateUser: String,
        email: String,
        password: String,
        isVerify: Boolean,
        isEmail: Boolean,
        isUpdate: Boolean = false
    ) {
        if (false)
            clear_login_session()
        editor.putBoolean(KEY_IS_LOGGEDIN, true)

        editor.putString(Key_Email, email)
        editor.putString(KEY_USER_PASSWORD, password)
        editor.putBoolean(KEY_IsVerify, isVerify)
        editor.putBoolean(Key_Email, isEmail)

        editor.putString(KEY_USER_OBJ, AuthenticateUser)
        editor.commit()
        Log.d(TAG, "User login session modified!")


    }

    public fun login_pass(password: String) {
        editor.putBoolean(KEY_IS_LOGGEDIN, true)
        editor.putString(KEY_USER_PASSWORD, password)
        editor.commit()

    }

    fun get_User_Pass(): String {

        val json = pref.getString(KEY_USER_PASSWORD, "")
        return json!!
    }

    public fun getPreferences(Key: String): String {
        return pref.getString(Key, "").toString()
    }

    public fun isLoggedIn(): Boolean {

        /*if ((!getIsVerify()!!))
            clear_login_session()*/
        return pref.getBoolean(KEY_IS_LOGGEDIN, false)
    }

    public fun isEmailLogin(): Boolean {

        return pref.getBoolean(Key_Email, false)
    }

    fun get_Authenticate_User(): SignupData {

        val gson = Gson()
        val json = pref.getString(KEY_USER_OBJ, "")
        return gson.fromJson(json, SignupData::class.java)
    }

    var userData: SignupData?
        get() {
            val gson = Gson()
            val json = pref.getString(KEY_USER_OBJ, "")
            return gson.fromJson(json, SignupData::class.java)
        }
        set(value) {
            val gson = Gson()
            val json = gson.toJson(value)
            editor.putString(KEY_USER_OBJ, json)
            editor.commit()
        }

    fun getWebLinks(): String? {
        return pref.getString(KEY_USER_WEBS, "")
    }

    fun setWebLinks(webs: String?) {
        editor.putString(KEY_USER_WEBS, webs)
        editor.commit()
    }


    var NotificationRead: Boolean
        get() = pref.getBoolean("NotificationRead", true)
        set(NotificationRead) {
            editor.putBoolean("NotificationRead", NotificationRead)
            editor.commit()
        }


    /*fun get_UserSettings(): UsersettingPojo.Datum {

        val gson = Gson()
        val json = pref.getString(KEY_USER_SETTINGS, "")
        return gson.fromJson<Any>(json, UsersettingPojo.Datum::class.java)
    }*/

    public fun set_UserSettings(defaultSettings: String) {

        editor.putString(KEY_USER_SETTINGS, defaultSettings)
        editor.commit()
        Log.d(TAG, "User settings modified!")
    }

    public fun get_Email(): String {
        return pref.getString(KEY_USER_EMAIL, "").toString()
    }

    public fun get_Password(): String {
        return pref.getString(KEY_USER_PASSWORD, "").toString()
    }

    public fun getIsVerify(): Boolean? {
        return pref.getBoolean(KEY_IsVerify, false)
    }

    public fun setIsVerify(isVerfiy: Boolean?) {
        editor.putBoolean(KEY_IsVerify, isVerfiy!!)
        // commit changes
        editor.commit()
        Log.d(TAG, "User login session modified!")
    }

//    public fun getsetSelectedLanguage(): String? {
//        return pref.getString(KEY_SelectedLanguageId, "")
//    }
//
//    public fun setSelectedLanguage(languageID: String?) {
//        editor.putString(KEY_SelectedLanguageId, languageID!!)
//        // commit changes
//        editor.commit()
//        Log.d(TAG, "User login session modified!")
//    }

    public fun getSelectedLanguage(): LanguageListData? {
        return Gson().fromJson<LanguageListData>(
            pref.getString(KEY_SelectedLanguage, ""),
            LanguageListData::class.java
        )
    }

    public fun setSelectedLanguage(language: LanguageListData?) {
        editor.putString(KEY_SelectedLanguage, Gson().toJson(language))
        editor.commit()
    }

    public fun clear_login_session() {
        editor.clear()
        editor.commit()
    }

    var dynamicStringData: String
        get() {
            val gson = Gson()
            val json = pref.getString(KEY_DynamicString, "")
            if (json!!.isEmpty())
                return String()

            return gson.fromJson(json, String()::class.java)
        }
        set(value) {
            val gson = Gson()
            val json = gson.toJson(value)
            editor.putString(KEY_DynamicString, json)
            editor.commit()
        }

    fun isQBUser(yesno: Boolean) {
        editor.putBoolean(KEY_IS_QBUser, yesno)
        editor.commit()
    }

    public fun getYesNoQBUser(): Boolean {
        return pref.getBoolean(KEY_IS_QBUser, false)
    }

    fun getQbUser(): QBUser? {
        if (getYesNoQBUser()) {
            val id = pref.getInt(QB_USER_ID, -1)
            val login = pref.getString(QB_USER_LOGIN, "")
            val password = pref.getString(QB_USER_PASSWORD, "")
            val fullName = pref.getString(QB_USER_FULL_NAME, "")
            val tagsInString = pref.getString(QB_USER_TAGS, "")

            var tags: StringifyArrayList<String>? = null

            if (tagsInString != null) {
                tags = StringifyArrayList()
                tags.add(
                    *tagsInString!!.split(",".toRegex()).dropLastWhile({ it.isEmpty() })
                        .toTypedArray()
                )
            }

            val user = QBUser(login, password)
            user.setId(id!!)
            user.fullName = fullName
            user.tags = tags
            return user
        } else {
            return null
        }
    }

    var LanguageLabel: LanguageLabelPojo.LanguageLabelData?
        get() {
            val gson = Gson()
            val json = pref.getString("LanguageLabel", "").toString()
            if (!json.isNullOrEmpty()) {
                return gson.fromJson(json, LanguageLabelPojo.LanguageLabelData::class.java)
            } else {
                return null
            }

        }
        set(LanguageLabel) {
            if (LanguageLabel != null) {
                val gson = Gson()
                editor.putString("LanguageLabel", gson.toJson(LanguageLabel))
                editor.commit()
            }
        }

    fun saveQbUser(qbUser: QBUser) {
        editor.putInt(QB_USER_ID, qbUser.id)
        editor.putString(QB_USER_LOGIN, qbUser.login)
        editor.putString(QB_USER_PASSWORD, qbUser.password)
        editor.putString(QB_USER_FULL_NAME, qbUser.fullName)
        editor.putString(QB_USER_TAGS, qbUser.tags.itemsAsString)
        editor.commit()
    }
}