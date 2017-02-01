package services.pos

object Tag {
  def apply(lemma: String): Tag = Tag(lemma, "", lemma)
}

case class Tag(token: String, tag: String, lemma: String) {
  override def toString: String = s"$token $tag $lemma"
}