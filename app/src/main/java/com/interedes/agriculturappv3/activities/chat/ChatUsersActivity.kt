package com.interedes.agriculturappv3.activities.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.util.UsersAdapter
import com.interedes.agriculturappv3.productor.models.chat.UserFirebase
import kotlinx.android.synthetic.main.activity_chat_users.*
import java.util.ArrayList

class ChatUsersActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mUsersDBRef: DatabaseReference? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var adapter: UsersAdapter? = null
    private val mUsersList = ArrayList<UserFirebase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_users)


        //assign firebase auth
        mAuth = FirebaseAuth.getInstance()
        mUsersDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        //initialize the recyclerview variables

        usersRecyclerView.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        usersRecyclerView.setLayoutManager(mLayoutManager)
    }


    private fun populaterecyclerView() {
        adapter = UsersAdapter(mUsersList)
        usersRecyclerView.setAdapter(adapter)

    }

    private fun queryUsersAndAddthemToList() {
        mUsersDBRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount > 0) {
                    for (snap in dataSnapshot.children) {
                        var user = snap.getValue<UserFirebase>(UserFirebase::class.java)
                        //if not current user, as we do not want to show ourselves then chat with ourselves lol
                        try {
                            var  auth= mAuth?.currentUser?.uid

                            if(!user?.User_Id.equals(auth))
                             {
                                mUsersList.add(user!!)
                             }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                /**populate listview */
                populaterecyclerView()
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        checkIfUserIsSignIn()
        /**query usrs and add them to a list */
        queryUsersAndAddthemToList()
    }


    private fun checkIfUserIsSignIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            /**go to login user first */
            goToSignIn()
        }
    }

    private fun goToSignIn() {
        //startActivity(Intent(this, LoginActivity::class.java))
    }


}
