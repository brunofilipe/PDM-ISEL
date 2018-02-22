package pdm.isel.moviedatabaseapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import pdm.isel.moviedatabaseapp.R
import pdm.isel.moviedatabaseapp.controller.model.MovieDto

class MovieAdapter(
        private val ctx: Context,
        private val resource: Int,
        private val items: MutableList<MovieDto>,
        private val imageLoader: ImageLoader
) : ArrayAdapter<MovieDto>(ctx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val rowView: View
        if (convertView == null) {
            val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = inflater.inflate(resource, parent, false)
            holder = fillHolder(rowView)
        } else {
            rowView = convertView
            holder = convertView.tag as ViewHolder
        }

        val item = items[position]
        holder.extraInfo.text = String.format("(%.1f/10) %s ", item.voteAverage, item.releaseDate)
        holder.movieTitle.text = item.title
        holder.imgView.setImageUrl(urlBuilder(item), imageLoader)
        return rowView
    }
    private fun urlBuilder(item: MovieDto): String? = if (item.poster == null) null else "http://image.tmdb.org/t/p/w185/${item.poster}?$"

     override fun add(item: MovieDto){
         items.add(item)
         notifyDataSetChanged()
     }

    private fun fillHolder(convertView: View): ViewHolder {
        val holder = ViewHolder(
                convertView.findViewById<NetworkImageView>(R.id.posterImageView) as NetworkImageView,
                convertView.findViewById<TextView>(R.id.movieTitle) as TextView,
                convertView.findViewById<TextView>(R.id.extraInfo) as TextView
        )
        holder.imgView.setDefaultImageResId(R.drawable.default_poster)
        convertView.tag = holder
        return holder
    }
}

class ViewHolder(
        val imgView: NetworkImageView,
        val movieTitle: TextView,
        val extraInfo: TextView
)