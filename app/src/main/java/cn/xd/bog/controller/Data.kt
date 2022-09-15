package cn.xd.bog.controller

import cn.xd.bog.entity.Content
import cn.xd.bog.entity.PlateContent
import cn.xd.bog.entity.PlateEntity
import cn.xd.bog.entity.SingleContent

interface Data {
    fun requestPlateList(): PlateEntity

    fun requestPlateContent(id: Int, pageIndex: Int, num: Int = 20): Pair<PlateContent?, String?>

    fun requestContent(id: Int, pageIndex: Int, num: Int = 20, order: Int = 0): Pair<Content?, String?>

    fun requestSingle(id: Int): SingleContent
}