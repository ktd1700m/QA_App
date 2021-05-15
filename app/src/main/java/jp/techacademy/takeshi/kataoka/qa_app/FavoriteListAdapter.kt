package jp.techacademy.takeshi.kataoka.qa_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.list_favorite.view.*

class FavoriteListAdapter(context: Context) : BaseAdapter() {
    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mFavoriteArrayList = ArrayList<Favorite>()

    override fun getCount(): Int {
        return mFavoriteArrayList.size
    }

    override fun getItem(position: Int): Any {
        return mFavoriteArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_favorite, parent, false)
        }

        val titleText = convertView!!.titleTextView as TextView
        titleText.text = mFavoriteArrayList[position].title

        val genreText = convertView.genreTextView as TextView
        genreText.text = when(mFavoriteArrayList[position].genre) {
            1 -> "趣味"
            2 -> "生活"
            3 -> "健康"
            4 -> "コンピューター"
            else -> ""
        }

        return convertView
    }

    fun setFavoriteArrayList(favoriteArrayList: ArrayList<Favorite>) {
        mFavoriteArrayList = favoriteArrayList
    }

}