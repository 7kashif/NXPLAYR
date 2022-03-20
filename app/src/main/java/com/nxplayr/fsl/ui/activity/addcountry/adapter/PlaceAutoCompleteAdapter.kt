package com.nxplayr.fsl.ui.activity.addcountry.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.compat.AutocompleteFilter
import com.google.android.libraries.places.compat.AutocompletePrediction
import com.google.android.libraries.places.compat.AutocompletePredictionBufferResponse
import com.google.android.libraries.places.compat.GeoDataClient
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PlaceAutoCompleteAdapter(private val mContext: Context,
                               private val mGeoDataClient: GeoDataClient,
                               private var mBounds: LatLngBounds,
                               var mPlaceFilter: AutocompleteFilter,
                               private val mPlaceClickInterface: PlaceAutoCompleteInterface
): RecyclerView.Adapter<PlaceAutoCompleteAdapter.PredictionHolder>(), Filterable {


    companion object {
        var mResultList = java.util.ArrayList<AutocompletePrediction>()
        private val STYLE_BOLD = StyleSpan(Typeface.BOLD)

    }

    interface PlaceAutoCompleteInterface {
        fun onPlaceClick(mResultList: ArrayList<AutocompletePrediction>, position: Int)
    }

    class PredictionHolder(holder: View) : RecyclerView.ViewHolder(holder) {
        val mAddress1: TextView = holder.findViewById(R.id.textViewCity)
        val mAddress2: TextView = holder.findViewById(R.id.textViewState)
        val mPredictionLayout: LinearLayout = holder.findViewById(R.id.predictedRow)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val results = FilterResults()

                var filterData: ArrayList<AutocompletePrediction>? = ArrayList()

                if (constraint != null) {

                    filterData = getAutoComplete(constraint)
                }

                results.values = filterData
                if (filterData != null) {
                    results.count = filterData.size
                } else {
                    results.count = 0
                }

                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {
                if (results != null && results.count > 0) {

                    mResultList = results.values as ArrayList<AutocompletePrediction>
                    notifyDataSetChanged()
                } else {

                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun getAutoComplete(constraint: CharSequence?): java.util.ArrayList<AutocompletePrediction>? {

        val results = mGeoDataClient.getAutocompletePredictions(constraint!!.toString(), mBounds,
                GeoDataClient.BoundsMode.BIAS, mPlaceFilter)
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

        try {
            var autocompletePredictions: AutocompletePredictionBufferResponse = results.result!!

            Log.i("Tag 1", "Query completed. Received " + autocompletePredictions.count
                    + " predictions.")


            return DataBufferUtils.freezeAndClose(autocompletePredictions)
        } catch (e: RuntimeExecutionException) {

            Log.e("Tag 2", "Error getting autocomplete prediction API call", e)
            return null
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionHolder {
        val mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = mLayoutInflater.inflate(R.layout.place_recyclerview_item, parent, false)
        return PredictionHolder(convertView)
    }

    override fun onBindViewHolder(holder: PredictionHolder, position: Int) {
        holder.mAddress1.text = mResultList[position].getFullText(STYLE_BOLD)
        holder.mAddress2.text = mResultList[position].getSecondaryText(STYLE_BOLD)

        holder.mPredictionLayout.setOnClickListener {
            mPlaceClickInterface.onPlaceClick(mResultList, position)
        }
    }

    override fun getItemCount(): Int {
        return if (mResultList != null) {
            mResultList.size
        } else {
            0
        }
    }

    fun getItem(position: Int): AutocompletePrediction {
        return mResultList[position]
    }
}