package com.mansoorali.i212502

data class chatMessageModel(
    var msgID:String,
    var recId:String,
    var id:String,
    var time:String,
    var uri:String,
    var message: String,
    var audio: String,
    var picture: String,
    var file: String,
    var type: String?
)

data class CommchatMessageModel(
    var msgID:String,
    var id:String,
    var time:String,
    var uri:String,
    var message: String,
    var audio: String,
    var picture: String,
    var file: String,
    var type: String?
)