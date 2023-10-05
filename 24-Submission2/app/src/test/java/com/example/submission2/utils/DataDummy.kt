package com.example.submission2.utils

import com.example.submission2.data.model.Story
import com.example.submission2.data.model.User

object DataDummy {

    fun generateDummyUser(): User {
        return User(
            userId = "userId",
            name = "name",
            token = "abcdef123456"
        )
    }

    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                id = i.toString(),
                name = "name $i",
                location = "location $i",
                description = "description $i",
                photoUrl = "photoUrl $i",
                createdAt = "createdAt $i",
                lat = i.toDouble(),
                lon = i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}