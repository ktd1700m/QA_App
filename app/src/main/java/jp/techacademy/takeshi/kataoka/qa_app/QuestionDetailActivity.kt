package jp.techacademy.takeshi.kataoka.qa_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_question_detail.*

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference

    private lateinit var mFavoriteRef: DatabaseReference

    private var isFavorite = false

    private val favoriteEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
            // flgをTRUEに
            isFavorite = true

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

    }

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<*, *>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val uid = map["uid"] as? String ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras!!.get("question") as Question

        title = mQuestion.title

        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        // ログイン済みのユーザーを取得する
        val user = FirebaseAuth.getInstance().currentUser

        fab.setOnClickListener {
            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)

            } else {
                // Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
            }
        }

        val databaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = databaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid)
            .child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)

/////////// 以下、課題提出用の追加コード ///////////

        if (user == null) {

        } else {
            val databaseReference = FirebaseDatabase.getInstance().reference
            mFavoriteRef = databaseReference.child(UsersPATH).child(user!!.uid).child(FavoritesPATH)
            isFavorite = false
            mFavoriteRef.addChildEventListener(favoriteEventListener)
        }

        fab_favorite.setOnClickListener {

            // ToDo: お気に入り済みなら削除・してないなら登録
            mFavoriteRef = databaseReference.child(UsersPATH)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(FavoritesPATH)

            if (isFavorite) {
              // 削除
                mFavoriteRef.removeValue()
                fab_favorite.setImageResource(R.drawable.ic_star_border)
                isFavorite = false
            } else {
                //val rootUid = dataSnapshot.key ?: ""
                //val data = HashMap<String, String>()
                //data["questionUid"] = mQuestion.questionUid
                mFavoriteRef.push().setValue(mQuestion.questionUid)
                fab_favorite.setImageResource(R.drawable.ic_star)

            }


        }
    }

    override fun onResume() {
        super.onResume()

        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userPath = databaseReference.child(UsersPATH)

        //mFavoriteRef = databaseReference.child(UsersPATH).child("DFETC0B1PKcMaUpCbtmnX7eLMH13")
        //mFavoriteRef = databaseReference.child(UsersPATH)
        //mFavoriteRef.child(FavoritesPATH).push().setValue("aaa1")
        //mFavoriteRef.child(FavoritesPATH).push().setValue("bbb2")

        if (user == null) {
            fab_favorite.visibility = View.INVISIBLE
        } else {
            fab_favorite.visibility = View.VISIBLE

            //for (favorite in userPath.child(user!!.uid).child(FavoritesPATH))
            //val data = userPath.child(user!!.uid).child(FavoritesPATH)

//            mFavoriteRef = databaseReference.child(UsersPATH).child(user.uid).child(FavoritesPATH)

//            isFavorite = false
//            mFavoriteRef.addChildEventListener(favoriteEventListener)


            /*
            Log.d("QA_testLog", "currentUser = $user ; " + mQuestion.uid )

            mFavoriteRef.get().addOnSuccessListener {
                Log.i("QA_testLog", "Got Value ${it.value}")
            }.addOnFailureListener {
                Log.e("QA_testLog", "Error getting data", it)
            }
            */
        }


    }
}