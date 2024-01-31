package com.example.capstone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.capstone.Dashboard.BarangayOfficials
import com.example.capstone.Dashboard.EmergencyContacts
import com.example.capstone.Message_.UserSendMessage
import com.example.capstone.Reservation.adminReservationView
import com.example.capstone.bottomMenu.ReservationList
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.message.Inbox
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class navigation : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var topMenuButton: ImageButton
    private lateinit var notificationIcon: ImageView
    private lateinit var fragmentNameTextView: TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var searchIcon: ImageView
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)
        preferences = getPreferences(Context.MODE_PRIVATE)
        fragmentNameTextView = binding.root.findViewById(R.id.FragmentName)
        notificationIcon = binding.root.findViewById(R.id.notif)
        searchIcon = binding.root.findViewById(R.id.search)

        val user = FirebaseAuth.getInstance().currentUser
        val bottomNavigationView = binding.bottomNavigationView
        replace(dashboard())

        // Set the initial fragment name
        updateFragmentName("Home")

        // Check the notification preference and set the icon accordingly
        val isNotificationEnabled = preferences.getBoolean("notification_enabled", true)
        updateNotificationIcon(isNotificationEnabled)

        // Set a click listener for the notification icon
        notificationIcon.setOnClickListener {
            showNotificationDialog()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        // Inside onCreate method after initializing your views
        val topMenuButton: ImageButton = binding.root.findViewById(R.id.topmenu)

        topMenuButton.setOnClickListener {
            // Open the drawer when the top menu button is clicked
            drawerLayout.openDrawer(GravityCompat.START)
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            val currentUser = auth.currentUser
            when (it.itemId) {
                R.id.profile -> {
                    // Handle the profile item click
                    updateFragmentName("Profile")
                    if (currentUser != null && "admin@email.com" == currentUser.email) {
                        val profileFragment = AdminProfile()
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.container, profileFragment)
                        transaction.addToBackStack(null)  // Optional: Add to back stack if you want to navigate back
                        transaction.commit()
                    } else {
                        val profileFragment = Profile()
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.container, profileFragment)
                        transaction.addToBackStack(null)  // Optional: Add to back stack if you want to navigate back
                        transaction.commit()
                    }
                }
                R.id.message -> {
                    updateFragmentName("Inbox")
                    if (currentUser != null && "admin@email.com" == currentUser.email) {
                        // If admin, replace with AdminMessage fragment
                        replace(Inbox())
                    } else {
                        // If not admin, start UserSendMessage activity
                        val intent = Intent(this, UserSendMessage::class.java)
                        startActivity(intent)
                    }
                }
                R.id.officials -> {
                    val intent = Intent(this, BarangayOfficials::class.java)
                    startActivity(intent)
                }
                R.id.contacts -> {
                    val intent = Intent(this, EmergencyContacts::class.java)
                    startActivity(intent)
                }
                R.id.switchAccount -> {
                    Firebase.auth.signOut()
                    val intent = Intent(this, LoginPage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.signout -> {
                    Firebase.auth.signOut()
                    val intent = Intent(this, LoginPage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                else -> {

                }
            }
            // Close the drawer after handling the item click
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        if (user?.email == LoginPage.ADMIN_EMAIL) {
            // Admin user, load admin layout
            bottomNavigationView.inflateMenu(R.menu.adminmenu)
        } else {
            // Regular user, load regular layout
            bottomNavigationView.inflateMenu(R.menu.usermenu)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replace(dashboard())
                    updateFragmentName("Home")
                }
                R.id.calendar -> {
                    replace(calendar())
                    updateFragmentName("Upcoming Events")
                }
                R.id.admincalendar -> {
                    replace(AdminCalendarView())
                    updateFragmentName("Events")
                }
                R.id.upload -> {
                    replace(Home())
                    updateFragmentName("Recent Reports")
                }
                R.id.navigation_read_report -> {
                    replace(AdminCheck())
                    updateFragmentName("New Reports")
                }
                R.id.userappointments -> {
                    replace(ReservationList())
                    updateFragmentName("My Appointments")
                }
                R.id.appointments -> {
                    replace(adminReservationView())
                    updateFragmentName("Appointments")
                }
                R.id.report -> {
                    replace(Home())
                    updateFragmentName("Recent Reports")
                }
                R.id.usermessage -> {
                    val currentUser = auth.currentUser
                    if (currentUser != null && "admin@email.com" == currentUser.email) {
                        // If admin, replace with AdminMessage fragment
                        replace(Inbox())
                    } else {
                        // If not admin, start UserSendMessage activity
                        val intent = Intent(this, UserSendMessage::class.java)// contextUnresolved reference: context
                        startActivity(intent)
                    }
                }
                else -> {

                }
            }
            true
        }
    }

    private fun replace(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            R.id.topmenu -> {
                // Open the drawer when the top menu button is clicked
                val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showNotificationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Notification Permission")
        builder.setMessage("Do you want to enable notifications?")

        builder.setPositiveButton("Accept") { _, _ ->
            // User accepted, enable notifications
            updateNotificationIcon(true)
            preferences.edit().putBoolean("notification_enabled", true).apply()
        }

        builder.setNegativeButton("Reject") { _, _ ->
            // User rejected, disable notifications
            updateNotificationIcon(false)
            preferences.edit().putBoolean("notification_enabled", false).apply()
        }

        builder.setOnCancelListener {
            // Handle cancel event if needed
        }

        builder.show()
    }

    private fun updateNotificationIcon(isEnabled: Boolean) {
        val iconResId = if (isEnabled) {
            R.drawable.notifications_on_icon
        } else {
            R.drawable.notifications_off_icon
        }

        notificationIcon.setImageResource(iconResId)

        // You can also change the tint color if needed
        val tintColor = ContextCompat.getColor(this, R.color.background)
        notificationIcon.setColorFilter(tintColor)
    }

    private fun updateFragmentName(name: String) {
        fragmentNameTextView.text = name

        // Check if the current fragment requires a search bar
        val requiresSearchBar = when (name) {
            "Upcoming Events", "Events", "Recent Reports", "New Reports", "Inbox", "My Appointments" -> true
            else -> false
        }

        // Change the visibility of the icons based on the conditions
        if (requiresSearchBar) {
            // Change the icon to a search bar icon
            searchIcon.visibility = View.VISIBLE
            notificationIcon.visibility = View.GONE
        } else {
            // Change the icon to notifications icon if needed
            notificationIcon.visibility = View.VISIBLE
            searchIcon.visibility = View.GONE
        }
    }
}
