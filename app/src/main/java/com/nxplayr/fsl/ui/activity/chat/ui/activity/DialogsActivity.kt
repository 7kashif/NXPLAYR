package com.nxplayr.fsl.ui.activity.chat.ui.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.view.ActionMode
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userconnection.view.ChatToConnectionListFragment
import com.nxplayr.fsl.ui.activity.chat.async.BaseAsyncTask
import com.nxplayr.fsl.ui.activity.chat.managers.DialogsManager
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBIncomingMessagesManager
import com.quickblox.chat.QBSystemMessagesManager
import com.quickblox.chat.exception.QBChatException
import com.quickblox.chat.listeners.QBChatDialogMessageListener
import com.quickblox.chat.listeners.QBSystemMessageListener
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.core.request.QBRequestGetBuilder
import com.quickblox.messages.services.QBPushManager
import com.quickblox.messages.services.SubscribeService
import com.nxplayr.fsl.ui.activity.chat.ui.adapter.DialogsAdapter
import com.nxplayr.fsl.ui.activity.chat.utils.ACTION_NEW_FCM_EVENT
import com.nxplayr.fsl.ui.activity.chat.utils.EXTRA_FCM_MESSAGE
import com.nxplayr.fsl.ui.activity.chat.utils.SharedPrefsHelper
import com.nxplayr.fsl.ui.activity.chat.utils.chat.ChatHelper
import com.nxplayr.fsl.ui.activity.chat.utils.qb.QbChatDialogMessageListenerImpl
import com.nxplayr.fsl.ui.activity.chat.utils.qb.QbDialogHolder
import com.nxplayr.fsl.ui.activity.chat.utils.qb.VerboseQbChatConnectionListener
import com.nxplayr.fsl.ui.activity.chat.utils.qb.callback.QBPushSubscribeListenerImpl
import com.nxplayr.fsl.ui.activity.chat.utils.qb.callback.QbEntityCallbackImpl
import com.nxplayr.fsl.ui.activity.chat.utils.shortToast
import com.nxplayr.fsl.util.MyUtils
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.android.synthetic.main.activity_dialogs.*
import org.jivesoftware.smack.ConnectionListener
import java.lang.ref.WeakReference

const val DIALOGS_PER_PAGE = 100

class DialogsActivity : Fragment(), DialogsManager.ManagingDialogsCallbacks {
    private val TAG = DialogsActivity::class.java.simpleName

    private lateinit var refreshLayout: SwipyRefreshLayout
    private lateinit var progress: ProgressBar
    private lateinit var menu: Menu
    private var isProcessingResultInProgress: Boolean = false
    private lateinit var pushBroadcastReceiver: BroadcastReceiver
    private lateinit var chatConnectionListener: ConnectionListener
     var v:View?=null
    private lateinit var dialogsAdapter: DialogsAdapter
    private var allDialogsMessagesListener: QBChatDialogMessageListener = AllDialogsMessageListener()
    private var systemMessagesListener: SystemMessagesListener = SystemMessagesListener()
    private  var systemMessagesManager: QBSystemMessagesManager?=null
    private  var incomingMessagesManager: QBIncomingMessagesManager?=null
    private var dialogsManager: DialogsManager = DialogsManager()
    private lateinit var currentUser: QBUser

    private var currentActionMode: ActionMode? = null
    private var hasMoreDialogs = true
    private val joinerTasksSet = HashSet<DialogJoinerAsyncTask>()
    var mActivity :Activity?=null
    companion object {
        private const val REQUEST_SELECT_PEOPLE = 174
        private const val REQUEST_DIALOG_ID_FOR_UPDATE = 165
        private const val PLAY_SERVICES_REQUEST_CODE = 9000

        fun start(context: Context) {
            val intent = Intent(context, DialogsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity=context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(v==null)
        {
            v= inflater.inflate(R.layout.activity_dialogs, container, false)

        }
        return v
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (ChatHelper.getCurrentUser() != null) {
            currentUser = ChatHelper.getCurrentUser()!!
        } else {
            //finish()
        }
        floating_btn_connection.setOnClickListener {

            var bundle = Bundle()
            bundle.putString("fromData", "chat")
            (activity as MainActivity).navigateTo(ChatToConnectionListFragment(), bundle, ChatToConnectionListFragment::class.java.name, true)

        }

        initUi()
        initConnectionListener()
    }


    override fun onResume() {
        super.onResume()
        if (ChatHelper.isLogged()) {
            checkPlayServicesAvailable()
            registerQbChatListeners()
            if (QbDialogHolder.dialogsMap.isNotEmpty()) {
                loadDialogsFromQb(true, true)
            } else {
                loadDialogsFromQb(false, true)
            }
        } else {
            reloginToChat()
        }
    }


    private fun reloginToChat() {
      //  MyUtils.showProgressDialog(mActivity!!,mActivity!!.getString(R.string.dlg_relogin))
        if (SharedPrefsHelper.hasQbUser()) {
            ChatHelper.loginToChat(SharedPrefsHelper.getQbUser()!!, object : QBEntityCallback<Void> {
                override fun onSuccess(aVoid: Void?, bundle: Bundle?) {
                    Log.d(TAG, "Relogin Successful")
                    checkPlayServicesAvailable()
                    registerQbChatListeners()
                    loadDialogsFromQb(false, false)
                }

                override fun onError(e: QBResponseException) {
                    Log.d(TAG, "Relogin Failed " + e.message)
                    MyUtils.dismissProgressDialog()
                }
            })
        }
    }

    private fun checkPlayServicesAvailable() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(mActivity!!)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_REQUEST_CODE).show()
            } else {
                Log.i(TAG, "This device is not supported.")
               // finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        ChatHelper.removeConnectionListener(chatConnectionListener)
      //  LocalBroadcastManager.getInstance(mActivity!!).unregisterReceiver(pushBroadcastReceiver)
    }

    override fun onStop() {
        super.onStop()
        cancelTasks()
        unregisterQbChatListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterQbChatListeners()
    }

    private fun registerQbChatListeners() {
        ChatHelper.addConnectionListener(chatConnectionListener)
        try {
            systemMessagesManager = QBChatService.getInstance().systemMessagesManager
        } catch (e: Exception) {
            Log.d(TAG, "Can not get SystemMessagesManager. Need relogin. " + e.message)
            reloginToChat()
            return
        }
        incomingMessagesManager = QBChatService.getInstance().incomingMessagesManager
        if (incomingMessagesManager == null) {
            reloginToChat()
            return
        }
        systemMessagesManager?.addSystemMessageListener(systemMessagesListener)
        incomingMessagesManager?.addDialogMessageListener(allDialogsMessagesListener)
        dialogsManager.addManagingDialogsCallbackListener(this)

        pushBroadcastReceiver = PushBroadcastReceiver()
        LocalBroadcastManager.getInstance(mActivity!!).registerReceiver(pushBroadcastReceiver,
                IntentFilter(ACTION_NEW_FCM_EVENT))
    }

    private fun unregisterQbChatListeners() {
        incomingMessagesManager?.removeDialogMessageListrener(allDialogsMessagesListener)
        systemMessagesManager?.removeSystemMessageListener(systemMessagesListener)
        dialogsManager.removeManagingDialogsCallbackListener(this)
    }

    private fun cancelTasks() {
        joinerTasksSet.iterator().forEach {
            it.cancel(true)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult with ResultCode: $resultCode RequestCode: $requestCode")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_SELECT_PEOPLE -> {
                    val selectedUsers = data?.getSerializableExtra(EXTRA_QB_USERS) as ArrayList<QBUser>
                    var chatName = data.getStringExtra(EXTRA_CHAT_NAME)
                    if (isPrivateDialogExist(selectedUsers)) {
                        selectedUsers.remove(ChatHelper.getCurrentUser())
                        val existingPrivateDialog = QbDialogHolder.getPrivateDialogWithUser(selectedUsers[0])
                        isProcessingResultInProgress = false
                        existingPrivateDialog?.let {
                            ChatActivity.startForResult(mActivity!!, REQUEST_DIALOG_ID_FOR_UPDATE, it)
                        }
                    } else {
                       MyUtils. showProgressDialog(mActivity!!,mActivity!!.getString(R.string.create_chat))
                        if (TextUtils.isEmpty(chatName)) {
                            chatName = ""
                        }
                        createDialog(selectedUsers, chatName!!)
                    }
                }
                REQUEST_DIALOG_ID_FOR_UPDATE -> {
                    if (data != null) {
                        val dialogId = data.getStringExtra(EXTRA_DIALOG_ID)
                        loadUpdatedDialog(dialogId!!)
                    } else {
                        isProcessingResultInProgress = false
                        loadDialogsFromQb(true, false)
                    }
                }
            }
        } else {
            updateDialogsAdapter()
        }
    }

    private fun isPrivateDialogExist(allSelectedUsers: ArrayList<QBUser>): Boolean {
        val selectedUsers = ArrayList<QBUser>()
        selectedUsers.addAll(allSelectedUsers)
        selectedUsers.remove(ChatHelper.getCurrentUser())
        return selectedUsers.size == 1 && QbDialogHolder.hasPrivateDialogWithUser(selectedUsers[0])
    }

    private fun loadUpdatedDialog(dialogId: String) {
        ChatHelper.getDialogById(dialogId, object : QbEntityCallbackImpl<QBChatDialog>() {
            override fun onSuccess(result: QBChatDialog, bundle: Bundle?) {
                QbDialogHolder.addDialog(result)
                updateDialogsAdapter()
                isProcessingResultInProgress = false
            }

            override fun onError(e: QBResponseException) {
                isProcessingResultInProgress = false
            }
        })
    }


    private fun logout() {
        if (QBPushManager.getInstance().isSubscribedToPushes) {
            QBPushManager.getInstance().addListener(object : QBPushSubscribeListenerImpl() {
                override fun onSubscriptionDeleted(deleted: Boolean) {
                    logoutREST()
                    QBPushManager.getInstance().removeListener(this)
                }
            })
            SubscribeService.unSubscribeFromPushes(mActivity!!)
        } else {
            logoutREST()
        }
    }

    private fun logoutREST() {
        Log.d(TAG, "SignOut")
        QBUsers.signOut().performAsync(null)
    }

    private fun initUi() {
        val emptyHintLayout = v?.findViewById<LinearLayout>(R.id.ll_chat_empty)
        val dialogsListView: ListView = v?.findViewById(R.id.list_dialogs_chats)!!
        refreshLayout = v?.findViewById(R.id.swipy_refresh_layout)!!
        progress = v?.findViewById(R.id.pb_dialogs)!!

        val dialogs = ArrayList(QbDialogHolder.dialogsMap.values)
        dialogsAdapter = DialogsAdapter(mActivity!!, dialogs)

        dialogsListView.emptyView = emptyHintLayout
        dialogsListView.adapter = dialogsAdapter

        dialogsListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedDialog = parent.getItemAtPosition(position) as QBChatDialog
            if (currentActionMode != null) {
                dialogsAdapter.toggleSelection(selectedDialog)
                var subtitle = ""
                if (dialogsAdapter.selectedItems.size != 1) {
                    subtitle = getString(R.string.dialogs_actionmode_subtitle, dialogsAdapter.selectedItems.size.toString())
                } else {
                    subtitle = getString(R.string.dialogs_actionmode_subtitle_single, dialogsAdapter.selectedItems.size.toString())
                }
                currentActionMode!!.subtitle = subtitle
                currentActionMode!!.menu.get(0).isVisible = (dialogsAdapter.selectedItems.size >= 1)
            } else if (ChatHelper.isLogged()) {
                MyUtils.showProgressDialog(mActivity!!,mActivity!!.getString(R.string.dlg_loading))
                ChatActivity.startForResult(mActivity!!, REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog)
            } else {
                MyUtils.showProgressDialog(mActivity!!,mActivity!!.getString(R.string.dlg_login))
                ChatHelper.loginToChat(currentUser,
                        object : QBEntityCallback<Void> {
                            override fun onSuccess(p0: Void?, p1: Bundle?) {
                                MyUtils.dismissProgressDialog()
                                ChatActivity.startForResult(mActivity!!, REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog)
                            }

                            override fun onError(e: QBResponseException?) {
                                MyUtils.dismissProgressDialog()
                                shortToast(R.string.login_chat_login_error)
                            }
                        })
            }
        }

        dialogsListView.setOnItemLongClickListener { parent, view, position, id ->
            val selectedDialog = parent.getItemAtPosition(position) as QBChatDialog
            dialogsAdapter.selectItem(selectedDialog)
            return@setOnItemLongClickListener true
        }

        refreshLayout.setOnRefreshListener {
            cancelTasks()
            loadDialogsFromQb(silentUpdate = true, clearDialogHolder = true)
        }
        refreshLayout.setColorSchemeResources(R.color.color_new_blue, R.color.random_color_2, R.color.random_color_3, R.color.random_color_7)
    }

    private fun createDialog(selectedUsers: ArrayList<QBUser>, chatName: String) {
        ChatHelper.createDialogWithSelectedUsers(selectedUsers, chatName,
                object : QBEntityCallback<QBChatDialog> {
                    override fun onSuccess(dialog: QBChatDialog, args: Bundle?) {
                        Log.d(TAG, "Creating Dialog Successful")
                        isProcessingResultInProgress = false
                        dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager!!, dialog)
                        val dialogs = ArrayList<QBChatDialog>()
                        dialogs.add(dialog)
                        QbDialogHolder.addDialogs(dialogs)
                        DialogJoinerAsyncTask(this@DialogsActivity, dialogs).execute()
                        ChatActivity.startForResult(mActivity!!, REQUEST_DIALOG_ID_FOR_UPDATE, dialog, true)
                        MyUtils.dismissProgressDialog()
                    }

                    override fun onError(error: QBResponseException) {
                        Log.d(TAG, "Creating Dialog Error: " + error.message)
                        isProcessingResultInProgress = false
                        MyUtils.dismissProgressDialog()
                        (activity as MainActivity).showSnackBar(mActivity!!.getString(R.string.dialogs_creation_error))
                    }
                }
        )
    }

    private fun loadDialogsFromQb(silentUpdate: Boolean, clearDialogHolder: Boolean) {
        isProcessingResultInProgress = true
        if (silentUpdate) {
            progress.visibility = View.VISIBLE
        } else {
            MyUtils.showProgressDialog(mActivity!!,mActivity!!.getString(R.string.dlg_loading))
        }

        val requestBuilder = QBRequestGetBuilder()
        requestBuilder.limit = DIALOGS_PER_PAGE
        requestBuilder.skip = if (clearDialogHolder) {
            0
        } else {
            QbDialogHolder.dialogsMap.size
        }

        ChatHelper.getDialogs(requestBuilder, object : QBEntityCallback<ArrayList<QBChatDialog>> {
            override fun onSuccess(dialogs: ArrayList<QBChatDialog>, bundle: Bundle?) {
                if (dialogs.size < DIALOGS_PER_PAGE) {
                    hasMoreDialogs = false
                }
                if (clearDialogHolder) {
                    QbDialogHolder.clear()
                    hasMoreDialogs = true
                }
                QbDialogHolder.addDialogs(dialogs)
                updateDialogsAdapter()

                val joinerTask = DialogJoinerAsyncTask(this@DialogsActivity, dialogs)
                joinerTasksSet.add(joinerTask)
                joinerTask.execute()

                disableProgress()
                if (hasMoreDialogs) {
                    loadDialogsFromQb(true, false)
                }
            }

            override fun onError(e: QBResponseException) {
                disableProgress()
                shortToast(e.message)
            }
        })
    }

    private fun disableProgress() {
        isProcessingResultInProgress = false
        MyUtils.dismissProgressDialog()
        refreshLayout.isRefreshing = false
        progress.visibility = View.GONE
    }

    private fun initConnectionListener() {
        val rootView: View = v?.findViewById(R.id.layout_root)!!
        chatConnectionListener = object : VerboseQbChatConnectionListener(rootView) {
            override fun reconnectionSuccessful() {
                super.reconnectionSuccessful()
                loadDialogsFromQb(false, true)
            }
        }
    }

    private fun updateDialogsAdapter() {
        val listDialogs = ArrayList(QbDialogHolder.dialogsMap.values)
        dialogsAdapter.updateList(listDialogs)
    }

    override fun onDialogCreated(chatDialog: QBChatDialog) {
        loadDialogsFromQb(true, true)
    }

    override fun onDialogUpdated(chatDialog: String) {
        updateDialogsAdapter()
    }

    override fun onNewDialogLoaded(chatDialog: QBChatDialog) {
        updateDialogsAdapter()
    }

    private inner class PushBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra(EXTRA_FCM_MESSAGE)
            Log.v(TAG, "Received broadcast " + intent.action + " with data: " + message)
            loadDialogsFromQb(false, false)
        }
    }

    private inner class SystemMessagesListener : QBSystemMessageListener {
        override fun processMessage(qbChatMessage: QBChatMessage) {
            dialogsManager.onSystemMessageReceived(qbChatMessage)
        }

        override fun processError(e: QBChatException, qbChatMessage: QBChatMessage) {

        }
    }

    private inner class AllDialogsMessageListener : QbChatDialogMessageListenerImpl() {
        override fun processMessage(dialogID: String, qbChatMessage: QBChatMessage, senderID: Int?) {
            Log.d(TAG, "Processing received Message: " + qbChatMessage.body)
            if (senderID != currentUser.id) {
                dialogsManager.onGlobalMessageReceived(dialogID, qbChatMessage)
            }
        }
    }

    private class DialogJoinerAsyncTask internal constructor(dialogsActivity: DialogsActivity,
                                                             private val dialogs: ArrayList<QBChatDialog>) : BaseAsyncTask<Void, Void, Void>() {
        private val activityRef: WeakReference<DialogsActivity> = WeakReference(dialogsActivity)

        @Throws(Exception::class)
        override fun performInBackground(vararg params: Void): Void? {
            if (!isCancelled) {
                ChatHelper.join(dialogs)
            }
            return null
        }

        override fun onResult(result: Void?) {
            if (!isCancelled && !activityRef.get()?.hasMoreDialogs!!) {
                activityRef.get()?.disableProgress()
            } else {
            }
        }

        override fun onException(e: Exception) {
            super.onException(e)
            if (!isCancelled) {
                Log.d("Dialog Joiner Task", "Error: $e")
                shortToast("Error: " + e.message)
            }
        }

        override fun onCancelled() {
            super.onCancelled()
            cancel(true)
        }
    }

    fun applySearch(searchKeyword: String) {
        dialogsAdapter!!.filter?.filter(searchKeyword.toString())
        dialogsAdapter?.notifyDataSetChanged()
    }
}