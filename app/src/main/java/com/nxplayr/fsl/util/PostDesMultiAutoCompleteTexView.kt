package com.nxplayr.fsl.util

import android.content.Context
import android.text.*
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.FriendListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel.FriendListModel
import com.nxplayr.fsl.data.model.FriendListData
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

/**
 * Created by dhavalkaka on 06/02/2018.
 */
class PostDesMultiAutoCompleteTexView : AppCompatMultiAutoCompleteTextView, TextWatcher {
    var mContext: Context? = null
    var friendListAdapter: FriendListAdapter? = null
    var friendList: ArrayList<FriendListData?>? = null
    var contentFriendsTagList: ArrayList<FriendListData?>? = null
    var userId: String? = null
    var languageId: String? = null
    var getFollowModel = FriendListModel()

    constructor(context: Context?) : super(context!!) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        init(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        init(context)
    }

    fun init(c: Context?) {
        mContext = c
        getFollowModel =
            ViewModelProvider(mContext!! as FragmentActivity).get(FriendListModel::class.java)
        friendList = ArrayList()
        contentFriendsTagList = ArrayList()
        val sessionManager =
            SessionManager(mContext!!)
        userId = sessionManager.get_Authenticate_User().userID
        languageId = sessionManager.get_Authenticate_User().languageID
        var inputType = inputType
        setRawInputType(EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE.inv().let {
            inputType = inputType and it; inputType
        })
        setAutoCompleteAdapter()
        addTextChangedListener(this)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        val string = s.toString()
        if (string.contains("@")) {
            val pos = start
            if (string.length > 1) {
                val search =
                    text.toString().substring(text.toString().lastIndexOf("@"))
                getAllFriendlist(search, userId, languageId)
            }
        }
    }

    override fun afterTextChanged(s: Editable) {}
    private fun setAutoCompleteAdapter() {
        friendListAdapter = FriendListAdapter(mContext!!, R.layout.friendlistitem, friendList!!)
        setAdapter(friendListAdapter)
        threshold = 0
        setTokenizer(SpaceTokenizer())
        onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            try {
                val builder = SpannableStringBuilder(text)
                val start = text.toString()
                    .lastIndexOf((friendList!![position]!!.userFirstName!! + friendList!![position]!!.userLastName).trim { it <= ' ' })
                val end =
                    start + (friendList!![position]!!.userFirstName!! + friendList!![position]!!.userLastName).trim { it <= ' ' }.length
                builder.setSpan(
                    BackgroundColorSpan(mContext!!.resources.getColor(R.color.grey)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                text = builder
                setSelection(text.length)

                contentFriendsTagList!!.add(friendList!![position])
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAllFriendlist(
        searchKeyword: String,
        userId: String?,
        languageId: String?
    ) {
        var searchKeyword = searchKeyword
        searchKeyword = searchKeyword.replace("\n", "")
        searchKeyword = searchKeyword.replace("@", "")
        if (searchKeyword.length > 1) {
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            try {

                jsonObject.put("loginuserID", userId)
                jsonObject.put("languageID", languageId)
                jsonObject.put("searchWord", searchKeyword)
                jsonObject.put("userID", userId)
                jsonObject.put("friendtype", "taglist")
                jsonObject.put("action", "friendlist")
                jsonObject.put("page", "0")
                jsonObject.put("pagesize", "100")
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            jsonArray.put(jsonObject)
            Log.e("System out", jsonArray.toString())

            getFollowModel.friendApi(jsonArray.toString(), "friend_list")
            getFollowModel.successFriend.observe(mContext!! as FragmentActivity) {
                if (it != null && it.isNotEmpty()) {
                    if (it[0].status == "true") {
                        friendList!!.clear()
                        friendList?.addAll(it.get(0).data!!)
                        if (text.toString().contains("@")) {
                            setAdapter(friendListAdapter)
                            friendListAdapter!!.notifyDataSetChanged()
                        }

                    }

                }

            }

        }
    }

    fun setEditData(text: String) {
        var text = text
        contentFriendsTagList!!.clear()
        val mat =
            Pattern.compile("<Shase>(.+?)<Chase>").matcher(text)
        while (mat.find()) {
            Log.d("match", mat.group())
            if (mat.group(1) != null && mat.group(1).length >= 1) {
                text = text.replace("<Shase>" + mat.group(1) + "<Chase>", "#" + mat.group(1))
            }
        }
        // create the pattern matcher
        val m =
            Pattern.compile("<SatRate>(.+?)<CatRate>").matcher(text)
        val matchesSoFar = 0
        // iterate through all matches
        while (m.find()) { // get the match
            if (m.group(1) != null && m.group(1).isNotEmpty() && m.group(1).contains(",")) {
                text = text.replace(
                    "<SatRate>" + m.group(1) + "<CatRate>",
                    m.group(1).toString().trim { it <= ' ' }.substring(
                        0,
                        m.group(1).indexOf(",")
                    ).trim { it <= ' ' }
                )

                val datum: FriendListData? = null
                datum?.userFirstName =
                    (m.group(1).trim { it <= ' ' }.substring(0, m.group(1).indexOf(",")))
                datum?.userID = (m.group(1).trim { it <= ' ' }
                    .substring(
                        m.group(1).trim { it <= ' ' }.indexOf(",") + 2,
                        m.group(1).trim { it <= ' ' }.length
                    ))
                var isadded = false
                for (i in contentFriendsTagList!!.indices) {
                    if (contentFriendsTagList!![i]!!.userID == datum?.userID) {
                        isadded = true
                        break
                    }
                }
                if (!isadded) contentFriendsTagList!!.add(datum!!)
            }
        }
        val builder = SpannableStringBuilder(text)
        for (i in contentFriendsTagList!!.indices) {
            val start = text
                .lastIndexOf((contentFriendsTagList!![i]!!.userFirstName!! + contentFriendsTagList!![i]!!.userLastName).trim { it <= ' ' })
            val end =
                start + (contentFriendsTagList!![i]!!.userFirstName!! + contentFriendsTagList!![i]!!.userLastName).trim { it <= ' ' }.length
            builder.setSpan(
                BackgroundColorSpan(mContext!!.resources.getColor(R.color.grey)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        removeTextChangedListener(this)
        setText(builder, BufferType.SPANNABLE)
        addTextChangedListener(this)
    }


    val tagText: String
        get() {
            var jo = text.toString()
            for (i in contentFriendsTagList!!.indices) {
                var j = i + 1
                while (j < contentFriendsTagList!!.size) {

                    if (contentFriendsTagList!![i]!!.userID == contentFriendsTagList!![j]!!.userID) {
                        contentFriendsTagList!!.removeAt(j)
                        j--
                    }
                    j++
                }
            }
            for (i in contentFriendsTagList!!.indices) {
                jo = jo.replace(
                    (contentFriendsTagList!![i]!!.userFirstName!! + contentFriendsTagList!![i]!!.userLastName).trim()
                        .toRegex(),
                    "<SatRate>" + (contentFriendsTagList!![i]!!.userFirstName!! + contentFriendsTagList!![i]!!.userLastName).trim()
                        .toString() + ",," + contentFriendsTagList!![i]!!.userID.toString() + "<CatRate>"
                )


            }
            return SpannableBuilder.replaceHashTag(jo)
        }

    inner class SpaceTokenizer : Tokenizer {
        override fun findTokenStart(text: CharSequence, cursor: Int): Int {
            var i = cursor
            while (i > 0 && text[i - 1] != ' ') {
                i--
            }
            while (i < cursor && text[i] == ' ') {
                i++
            }
            return i
        }

        override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
            var i = cursor
            val len = text.length
            while (i < len) {
                if (text[i] == ' ') {
                    return i
                } else {
                    i++
                }
            }
            return len
        }

        override fun terminateToken(text: CharSequence): CharSequence {
            var i = text.length
            while (i > 0 && text[i - 1] == ' ') {
                i--
            }
            return if (i > 0 && text[i - 1] == ' ') {
                text
            } else {
                if (text is Spanned) {
                    val sp = SpannableString("$text ")
                    TextUtils.copySpansFrom(
                        text, 0, text.length,
                        Any::class.java, sp, 0
                    )
                    sp
                } else {
                    "$text "
                }
            }
        }
    }
}