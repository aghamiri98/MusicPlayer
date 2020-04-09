package tooka.io.musicplayer

import android.content.Context
import android.util.Log
import android.widget.Toast

fun Context.showToast(msg:String){
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()}
fun Context.logi(msg: String){
    Log.i("test",msg)
}
fun Context.loge(msg: String){
    Log.e("test",msg)
}
fun Context.logd(msg: String){
    Log.d("test",msg)
}
