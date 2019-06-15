/**************************************************************************
*
* Copyright (C) 2012-2015 Alex Taradov <alex@taradov.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*************************************************************************/

package com.example.simple_alarm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmNotification extends Activity
{
private final String TAG = "AlarmMe";
private Ringtone mRingtone;
private Vibrator mVibrator;
private final long[] mVibratePattern = { 0, 500, 500 };
public boolean mVibrate;
public Uri mAlarmSound;
public long mPlayTime;
public Timer mTimer = null;
private Alarm mAlarm;
private DateTime mDateTime;
private TextView mTextView;
private PlayTimerTask mTimerTask;

@Override
protected void onCreate(Bundle bundle)
{
  super.onCreate(bundle);

  getWindow().addFlags(
    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

  setContentView(R.layout.notification);

  mDateTime = new DateTime(this);
  mTextView = (TextView)findViewById(R.id.alarm_title_text);

  readPreferences();

  mRingtone = RingtoneManager.getRingtone(getApplicationContext(), mAlarmSound);
  if (mVibrate)
    mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

  start(getIntent());
}

@Override
protected void onDestroy()
{
  super.onDestroy();
  Log.i(TAG, "AlarmNotification.onDestroy()");

  stop();
}

@TargetApi(Build.VERSION_CODES.O)
@RequiresApi(api = Build.VERSION_CODES.O)
@Override
protected void onNewIntent(Intent intent)
{
  super.onNewIntent(intent);
  Log.i(TAG, "AlarmNotification.onNewIntent()");

  addNotification(mAlarm);

  stop();
  start(intent);
}

private void start(Intent intent)
{
  mAlarm = new Alarm(this);
  mAlarm.fromIntent(intent);

  Log.i(TAG, "AlarmNotification.start('" + mAlarm.getTitle() + "')");

  mTextView.setText(mAlarm.getTitle());

  mTimerTask = new PlayTimerTask();
  mTimer = new Timer();
  mTimer.schedule(mTimerTask, mPlayTime);
  mRingtone.play();
  if (mVibrate)
    mVibrator.vibrate(mVibratePattern, 0);
}

private void stop()
{
  Log.i(TAG, "AlarmNotification.stop()");

  mTimer.cancel();
  mRingtone.stop();
  if (mVibrate)
    mVibrator.cancel();
}
private String getQuestionThreeUserInput () {
  EditText userInputLastName = (EditText) findViewById(R.id.answerInputUserLastName);
  String name = userInputLastName.getText().toString();
  return name;
}
private void checkQuestionThreeAnswer () {
  String name = getQuestionThreeUserInput();
  if (name.trim().equalsIgnoreCase("waleed")) {
    finish();
  }
  else
  {

    Toast toast = Toast.makeText(AlarmNotification.this, " Erorr ..... . ", Toast.LENGTH_LONG);
    toast.show();
  }
}





public void onDismissClick(View view)

{  //Intent intent =new Intent(this,MainActivity.class);
//   startActivity(intent);
  checkQuestionThreeAnswer();

}
  public void onAskClick(View view)
  {
      Intent intent =new Intent(this,MainActivity.class);
      startActivity(intent);

  }

private void readPreferences()
{
  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

  mAlarmSound = Uri.parse(prefs.getString("alarm_sound_pref", "DEFAULT_RINGTONE_URI"));
  mVibrate = prefs.getBoolean("vibrate_pref", true);
  mPlayTime = (long)Integer.parseInt(prefs.getString("alarm_play_time_pref", "30")) * 1000;
}

@RequiresApi(api = Build.VERSION_CODES.O)
private void addNotification(Alarm alarm)
{
  NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
  Notification notification;
  PendingIntent activity;
  Intent intent;

  Log.i(TAG, "AlarmNotification.addNotification(" + alarm.getId() + ", '" + alarm.getTitle() + "', '" + mDateTime.formatDetails(alarm) + "')");

  intent = new Intent(this.getApplicationContext(), AlarmMe.class);
  intent.setAction(Intent.ACTION_MAIN);
  intent.addCategory(Intent.CATEGORY_LAUNCHER);

  activity = PendingIntent.getActivity(this, (int)alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

  NotificationChannel channel = new NotificationChannel("alarmme_01", "AlarmMe Notifications",
      NotificationManager.IMPORTANCE_DEFAULT);

  notification = new Builder(this)
      .setContentIntent(activity)
      .setSmallIcon(R.drawable.ic_notification)
      .setAutoCancel(true)
      .setContentTitle("Missed alarm: " + alarm.getTitle())
      .setContentText(mDateTime.formatDetails(alarm))
      .setChannelId("alarmme_01")
      .build();

  notificationManager.createNotificationChannel(channel);

  notificationManager.notify((int)alarm.getId(), notification);
}

@Override
public void onBackPressed()
{
  finish();
}

private class PlayTimerTask extends TimerTask
{
  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  public void run()
  {
    Log.i(TAG, "AlarmNotification.PalyTimerTask.run()");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      addNotification(mAlarm);
    }
    finish();
  }
}
}

