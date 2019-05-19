package com.example.minimoneybox

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import java.util.regex.Pattern
import kotlin.concurrent.thread
import android.os.AsyncTask
import android.util.JsonReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject
import java.io.*
import android.content.Intent
import android.widget.TextView
import org.json.JSONArray


/**
 * A login screen that offers login via email/password.
 */
class HomeAreaActivity : AppCompatActivity() {
    lateinit var tv_fname: TextView
    lateinit var tv_total_plan_val: TextView
    lateinit var tv_stocks_shares: TextView
    lateinit var tv_gia: TextView
    lateinit var tv_lifetime_isa: TextView
    private var bearerToken=""
    private var investorProdID = 0

    //to hold info for each product
    var s_and_s_id = 0
    var s_and_s_plan_value = 0
    var s_and_s_moneybox = 0
    var gia_id = 0
    var gia_plan_value = 0
    var gia_moneybox = 0
    var lifetime_isa_id = 0
    var lifetime_isa_plan_value = 0
    var lifetime_isa_moneybox = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_area)
        //get passed variables
        val intent = intent
        val fName = intent.getStringExtra("fname")
        bearerToken = intent.getStringExtra("bearerToken")
        println(bearerToken)
        //set initial name textview
        tv_fname = findViewById(R.id.tv_welcome_msg)
        tv_fname.text = "Hello $fName!"
    }

    override fun onStart() {
        super.onStart()

        //network task execute on separate thread
        AsyncTask.execute {
            run {
                // Create URL
                val moneyboxEndpoint = URL("https://api-test01.moneyboxapp.com/investorproducts")
                // Create connection
                val myConnection = moneyboxEndpoint.openConnection() as HttpsURLConnection
                myConnection.setRequestProperty("AppId", "3a97b932a9d449c981b595")
                myConnection.setRequestProperty("Content-Type", "application/json")
                myConnection.setRequestProperty("appVersion", "5.10.0")
                myConnection.setRequestProperty("apiVersion", "3.0.0")
                myConnection.setRequestProperty("Authorization", "Bearer $bearerToken")

                //read response
                if (myConnection.responseCode == 200) {
                    println("success!")
                    //read json response
                    val bR = BufferedReader(InputStreamReader(myConnection.inputStream))
                    var line: String? = null
                    val responseStrBuilder = StringBuilder()
                    while ({ line = bR.readLine(); line }() != null) {
                        responseStrBuilder.append(line)
                    }
                    myConnection.inputStream.close()
                    getInfo(JSONObject(responseStrBuilder.toString()))//get all required info from json response
                    runOnUiThread{
                        setDetails()//set onscreen text views etc...
                    }

                }else {
                    println("Error retrieving user data:\n" +myConnection.responseCode.toString() + "\n" + myConnection.responseMessage.toString())
                }
                myConnection.disconnect()
            }//end run
        } //end async

    }

    private fun getInfo(result: JSONObject){
        val productArray = JSONArray(result.getString("ProductResponses"))
        for (i in 0 until productArray.length()) {
            val product = JSONObject(productArray.get(i).toString())
            val productType = JSONObject(product.getString("Product"))
            investorProdID = product.getString("Id").toInt()
            //to allocate to correct variables
            if(productType.getString("Type").toLowerCase() == "isa"){
                s_and_s_id = investorProdID
                s_and_s_plan_value = product.getInt("PlanValue")
                s_and_s_moneybox = product.getInt("Moneybox")
            }else if(productType.getString("Type").toLowerCase() == "gia"){
                gia_id = investorProdID
                gia_plan_value = product.getInt("PlanValue")
                gia_moneybox = product.getInt("Moneybox")
            }else{
                lifetime_isa_id = investorProdID
                lifetime_isa_plan_value = product.getInt("PlanValue")
                lifetime_isa_moneybox = product.getInt("Moneybox")
            }
        }


        println("***************************************************************************************************************")
        println("Stocks and shares: ID - $s_and_s_id, plan value - $s_and_s_plan_value, moneybox - $s_and_s_moneybox")
        println("GIA: ID - $gia_id, plan value - $gia_plan_value, moneybox - $gia_moneybox")
        println("Lifetime ISA: ID - $lifetime_isa_id, plan value - $lifetime_isa_plan_value, moneybox - $lifetime_isa_moneybox")
        println("----------------------------------------------------------------------------------------------------------------")
        println("TOTAL PLAN VALUE: " + (s_and_s_plan_value+gia_plan_value+lifetime_isa_plan_value))
        println("***************************************************************************************************************")


    }

    private fun setDetails(){
        //set textviews
        tv_total_plan_val = findViewById(R.id.tv_plan_value)
        tv_total_plan_val.text = "Total Plan Value: £" +(s_and_s_plan_value+gia_plan_value+lifetime_isa_plan_value).toString()//TODO: use string resource, improve

        tv_stocks_shares = findViewById(R.id.tv_btn_stocks_shares)
        tv_stocks_shares.text = "Stocks and Shares ISA\n\nPlan Value: £$s_and_s_plan_value\n\nMoneybox: £$s_and_s_moneybox"
        tv_gia = findViewById(R.id.tv_btn_gia)
        tv_gia.text = "General Investment Account\n\nPlan Value: £$gia_plan_value\n\nMoneybox: £$gia_moneybox"
        tv_lifetime_isa = findViewById(R.id.tv_btn_isa)
        tv_lifetime_isa.text = "Lifetime ISA\n\nPlan Value: £$lifetime_isa_plan_value\n\nMoneybox: £$lifetime_isa_moneybox"

        //send to new screen if clicked on
        tv_stocks_shares.setOnClickListener{
            //send to next screen
            val intent = Intent(this@HomeAreaActivity, AddMoneyActivity::class.java)
            intent.putExtra("prod_id",s_and_s_id)
            intent.putExtra("acc_name", "Stocks and Shares ISA")
            intent.putExtra("plan_val",s_and_s_plan_value)
            intent.putExtra("moneybox_val",s_and_s_moneybox)
            intent.putExtra("bearerToken",bearerToken.trim())
            this@HomeAreaActivity.startActivity(intent)
        }
        tv_gia.setOnClickListener{
            //send to next screen
            val intent = Intent(this@HomeAreaActivity, AddMoneyActivity::class.java)
            intent.putExtra("prod_id",gia_id)
            intent.putExtra("acc_name", "General Investment Account")
            intent.putExtra("plan_val",gia_plan_value)
            intent.putExtra("moneybox_val",gia_moneybox)
            intent.putExtra("bearerToken",bearerToken.trim())
            this@HomeAreaActivity.startActivity(intent)
        }
        tv_lifetime_isa.setOnClickListener{
            //send to next screen
            val intent = Intent(this@HomeAreaActivity, AddMoneyActivity::class.java)
            intent.putExtra("prod_id",lifetime_isa_id)
            intent.putExtra("acc_name", "Lifetime ISA")
            intent.putExtra("plan_val",lifetime_isa_plan_value)
            intent.putExtra("moneybox_val",lifetime_isa_moneybox)
            intent.putExtra("bearerToken",bearerToken.trim())
            this@HomeAreaActivity.startActivity(intent)
        }


    }

}
