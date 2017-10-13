package com.mrsgx.campustalk.data.Local

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Shao on 2017/10/1.
 */
class SQLiteHelper(context: Context,name:String): SQLiteOpenHelper(context,name,null,1) {

    override fun onCreate(db: SQLiteDatabase) {
        createUserDB(db)
        createAreaDB(db)
        createSchoolDB(db)
        createLocationDB(db)
    }
    //创建用户表
    private fun createUserDB(db: SQLiteDatabase) {
        val sql = "CREATE TABLE " + TableUser.TABLE_USER_NAME +
                "(" +
                TableUser.UID + " varchar primary key ," +
                TableUser.EMAIL + " text , " +
                TableUser.NICKNAME + " text , " +
                TableUser.PASSWORD + " text , " +
                TableUser.SEX + " text , " +
                TableUser.AGE + " text , " +
                TableUser.SCHOOLCODE + " text , " +
                TableUser.USEREXPLAIN + " text , " +
                TableUser.STUCARD + " text , " +
                TableUser.STATE + " text , " +
                TableUser.HEADPIC + " text" +
                ")"
        db.execSQL(sql)
    }

    //创建地区表
    private fun createAreaDB(db: SQLiteDatabase){
        val sql = "CREATE TABLE " + TableArea.TABLE_AREA_NAME +
                "(" +
                TableArea.AREACODE + " text ," +
                TableArea.AREANAME + " text " +
                ")"
        db.execSQL(sql)
    }

    //创建学校表
    private fun createSchoolDB(db: SQLiteDatabase){
        val sql = "CREATE TABLE " + TableSchool.TABLE_SCHOOL_NAME +
                "(" +
                TableSchool.SCHOOL_CODE + " text ," +
                TableSchool.SCHOOL_NAME + " text ," +
                TableSchool.AREACODE + " text " +
                ")"
        db.execSQL(sql)
    }

    //创建坐标表
    private fun createLocationDB(db: SQLiteDatabase){
        val sql = "CREATE TABLE " + TableLocation.TABLE_LOCATION_NAME +
                "(" +
                TableLocation.LATITUDE + " text ," +
                TableLocation.LONGITUDE + " text ," +
                TableLocation.UID + " text, " +
                TableLocation.TIME + " text " +
                ")"
        println(sql)
        db.execSQL(sql)
    }
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
class TableUser {
    companion object {
        var TABLE_USER_NAME = "users"
        val UID = "Uid"
        val EMAIL = "email"
        val NICKNAME = "nickname"
        val PASSWORD = "password"
        val SEX = "sex"
        val HEADPIC = "headpic"
        val AGE = "age"
        val SCHOOLCODE = "schoolcode"
        val USEREXPLAIN = "userexplain"
        val STUCARD = "stucard"
        val STATE = "state"
    }
}
class TableArea {
    companion object {
        var TABLE_AREA_NAME = "areainfo"
        var AREACODE="Areacode"
        var AREANAME="areaname"
    }
}
class TableSchool {
    companion object {
        var TABLE_SCHOOL_NAME = "schoolinfo"
        var SCHOOL_CODE="SchoolCode"
        var SCHOOL_NAME="SchoolName"
        var AREACODE="AreaCode"
    }
}
class TableLocation {
    companion object {
        var TABLE_LOCATION_NAME = "Location"
        var LATITUDE="Latitude"
        var LONGITUDE="Longitude"
        var UID="Uid"
        var TIME="time"
    }
}