object WorkingWithAPI{
  def main(args: Array[String]) {
    import requests._
    import upickle.default._
    val tokenUrl = "https://accounts.spotify.com/api/token"
    val clientId = "0851e3454fe843b59f84f8c48d66544d"
    val clientSecret = "de1bce571d4f4a9484ecba07223938e3"

    val tokenResponse = requests.post(
      tokenUrl,
      data = Map(
        "grant_type" -> "client_credentials",
        "client_id" -> clientId,
        "client_secret" -> clientSecret
      )
    )


    //tokenResponse.text() is jsonString that has to be created into a memory object
    // ujson.read is the library that will do that for us

    val jsonResponse = ujson.read(tokenResponse.text())
    val access_token = jsonResponse("access_token").str


    val playlistId = "5Rrf7mqN8uus2AaQQQNdc1"
    val playlistApiUrl = s"https://api.spotify.com/v1/playlists/$playlistId"

    // Make a GET request to fetch the playlist data
    val playlistResponse = requests.get(
      playlistApiUrl,
      headers = Map("Authorization" -> s"Bearer ${access_token}")
    )

    //mapping each item to the track key of each item
    val playlistJson = ujson.read(playlistResponse.text)
    val tracks = playlistJson("tracks")("items").arr.map(_ ("track"))

    // Extracting the top 10 longest songs  from an array of tracks
    // Each track has a name, duration_ms
    // sort the track objects based on duration_ms attribute

    //val sortedByAge = people.sortBy(person => person.age)
    val top10LongestSongs = tracks.sortBy(_ ("duration_ms").num).takeRight(10)

    // Displaying the top 10 longest songs
    println("Top 10 Longest Songs:")
    top10LongestSongs.foreach { song =>
      val songName = song("name").str
      val durationMs = song("duration_ms").num
      println(s"$songName , $durationMs")
    }

    // Fetching and displaying artist details for each artist in the top 10 longest songs
    println("\nArtist Details:")
    val artists = top10LongestSongs.flatMap(song => song("artists").arr).distinct

    // Define a function to fetch follower count


    def getFollowerCount(artist: ujson.Value): (String, Int) = {
      val artistId = artist("id").str
      val artistApiUrl = s"https://api.spotify.com/v1/artists/$artistId"

      val artistResponse = requests.get(
        artistApiUrl,
        headers = Map("Authorization" -> s"Bearer $access_token")
      )

      val artistJson = ujson.read(artistResponse.text)
      val artistName = artistJson("name").str
      val followerCount = artistJson("followers")("total").num.toInt
      (artistName, followerCount)
    }

    // Use map to transform each artist into a tuple of (artistName, followerCount)
    val artistFollowers: Seq[(String, Int)] = artists.map(getFollowerCount).toSeq.sortBy(_._2)

    artistFollowers.foreach {
      case (artistName, artistFollowers) =>
        println(s"$artistName , $artistFollowers")
    }


  }

}
