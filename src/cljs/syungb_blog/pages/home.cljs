(ns syungb-blog.pages.home)

(defn home-page []
  [:div
   [:p
    "Not sure if I actually need home section in this website, but
    it doesn't hurt to have something until I get to be inspired to
    fill this page with something one random day. Until then, this
    page lives.."]
   [:p "And, enjoy a couple of pictures of my cats, Ddeok and Chichi :)"]
   [:img.home-page__img
    {:src "images/ddeok_and_chichi.jpg"
     :alt "My cat, Ddeok and Chichi"}]])
