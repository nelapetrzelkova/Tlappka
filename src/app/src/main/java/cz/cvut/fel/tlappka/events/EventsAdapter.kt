package cz.cvut.fel.tlappka.events

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import cz.cvut.fel.tlappka.R
import kotlinx.android.synthetic.main.event_item.view.*


class EventsAdapter(private val eventsList: List<EventItem>, private val mCtx: Context) : RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.event_list_name
        val date: TextView = itemView.event_list_date
        val time: TextView = itemView.event_list_time
        val type: TextView = itemView.event_list_type
        val privacy: TextView = itemView.event_list_privacy
        val GPSTracking: TextView = itemView.event_list_GPS
        val description_text: TextView = itemView.event_list_description
        val inProgress: TextView = itemView.in_progress_text
        val options: TextView = itemView.event_options
        val cardView: CardView = itemView.card_view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return MyViewHolder(v)
    }
    override fun getItemCount(): Int {
        return eventsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = eventsList[position]
        holder.eventName.text = currentItem.name
        holder.date.text = currentItem.date.toString()
        holder.time.text = currentItem.time.toString()
        holder.inProgress.text = getProgressString(currentItem.in_progress)
        holder.type.text = currentItem.type
        holder.privacy.text = getPrivacyString(currentItem.private)
        holder.GPSTracking.text = getGPSstring(currentItem.GPS_tracking)
        holder.description_text.text = currentItem.description
        if (holder.description_text.text.equals("")) {
            holder.description_text.visibility = View.GONE
        }
        holder.cardView.setTag(position)
        holder.options.setOnClickListener(View.OnClickListener {
            val popup =
                PopupMenu(mCtx, holder.options)
            popup.inflate(R.menu.event_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit_item ->
                        true
                    R.id.end_item ->
                        true
                    R.id.delete_item -> {
//                        deleteItem(currentItem.name)
                        true
                    }
                    else -> false
                }
            }
            //displaying the popup
            popup.show()
        })
    }

    private fun deleteItem(name: String?) {
        val db = FirebaseDatabase.getInstance()
        val myRef = db.getReference("Events").child(FirebaseAuth.getInstance().currentUser!!.uid)
        val query: Query = myRef.orderByChild("name").equalTo(name)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.w("User", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.ref.removeValue()
            }
        })
    }
    
    private fun getProgressString(inProgress: Boolean?): CharSequence? {
        return when (inProgress) {
            true -> "Právě probíhá"
            else -> ""
        }
    }

    private fun getGPSstring(gpsTracking: Boolean?): CharSequence? {
        return when (gpsTracking) {
            true -> "Sledování pomocí GPS zapnuto"
            else -> "Sledování pomocí GPS vypnuto"
        }
    }

    private fun getPrivacyString(private: Boolean?): CharSequence? {
        return when (private) {
            true -> "Soukromá událost"
            else -> "Veřejná událost"
        }
    }
}