package leo.me.la.lab3

object Data {
    val presidents: List<President> = listOf(
        President("Kaarlo Stahlberg", 1919, 1925, "Eka presidentti"),
        President("Lauri Relander", 1925, 1931, "Toka presidentti"),
        President("P. E. Svinhufvud", 1931, 1937, "Kolmas presidentti"),
        President("Kyösti Kallio", 1937, 1940, "Kolmas presidentti"),
        President("Risto Ryti", 1940, 1944, "Kolmas presidentti"),
        President("Carl Gustaf Emil Mannerheim", 1944, 1946, "Kuudes presidentti"),
        President("Juho Kusti Paasikivi", 1946, 1956, "Äkäinen ukko"),
        President("Urho Kekkonen", 1956, 1982, "Pelimies"),
        President("Mauno Koivisto", 1982, 1994, "Kolmas presidentti"),
        President("Martti Ahtisaari", 1994, 2000, "Kolmas presidentti"),
        President("Tarja Halonen", 2000, 2012, "Kolmas presidentti")
    )
}

data class President(val name: String, val start: Int, val end: Int, val description: String) {
    override fun toString(): String {
        return "$name $description $start $end"
    }
}
