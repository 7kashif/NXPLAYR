package com.nxplayr.fsl.util

import android.content.Context
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.util.Log
import com.nxplayr.fsl.data.model.Contact
import java.util.*

/**
 * Created by ADMIN on 07/02/2018.
 */
object ReadContacts {
    var totalcontacts = 0
    var cus_pos = 0
    var contactArrayList = ArrayList<Contact>()
    fun readContacts(context: Context): ArrayList<Contact> {
        contactArrayList = ArrayList()
        var getsynccontacts: Contact
        val sb = StringBuffer()
        sb.append("......Contact Details.....")
        val cr = context.contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.HAS_PHONE_NUMBER, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC")
        var phone = ""
        var emailContact = ""
        var emailType = ""
        var image_uri: String? = ""
        var bitmap: Bitmap
        var birthday: String
        val synclistsize = 0
        Log.i("System out", "contact size " + cur!!.count)
        totalcontacts = cur.count
        if (cur.count > 0) {
            while (cur.moveToNext()) {
                getsynccontacts = Contact()
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                var name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                Log.i("System out", "Name $name")
                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                name = name.substring(0, 1).toUpperCase() + name.substring(1)
                getsynccontacts.contactFirstname = name
                getsynccontacts.contactLastname = ""
                if (cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                    Log.i("System out", "name : $name, ID : $id")
                    sb.append("\n Contact Name:$name")
                    val pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    while (pCur!!.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        phone = phone.replace("?", "")
                        phone = phone.replace("-", "")
                        phone = phone.replace(" ", "")
                        if (phone.startsWith("+91")) {
                            phone = phone.trim { it <= ' ' }.substring(3)
                            Log.d("System out", "omitting success : $phone")
                        }
                        if (phone != null) getsynccontacts.phone = phone else getsynccontacts.phone = ""
                        sb.append("\n Phone number:$phone")
                        Log.i("System out", "phone$phone")
                        cus_pos++
                    }
                    pCur.close()
                }
                val emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null)
                while (emailCur!!.moveToNext()) {
                    emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))
                    getsynccontacts.emailaddress = emailContact
                    sb.append("""

    Email:${emailContact}Email type:$emailType
    """.trimIndent())
                    Log.i("System out", "Email $emailContact Email Type : $emailType")
                }
                emailCur.close()
                contactArrayList.add(getsynccontacts)
            }
        }
        return contactArrayList
    }
}