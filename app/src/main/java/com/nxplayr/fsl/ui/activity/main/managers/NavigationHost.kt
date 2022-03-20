package com.nxplayr.fsl.ui.activity.main.managers

import android.os.Bundle
import androidx.fragment.app.Fragment



interface NavigationHost {
    /**
     * Trigger a navigation to the specified fragment, optionally adding a transaction to the back
     * stack to make this navigation reversible.
     */

    fun navigateTo(fragment: Fragment, string: String, addToBackstack: Boolean)
    fun navigateToBusiness(fragment: Fragment, string: String, addToBackstack: Boolean)
    fun navigateTo(fragment: Fragment, bundle: Bundle, string:String, addToBackstack: Boolean)


}