(ns syungb-blog.pages.about)

(def intro-map
  {"studied astrophysics"
   "dream was to work at nasa, but didn't dream of being an astronaut due to bad eyesight"

   "have two cats"
   "names are ddeok and chichi"

   "korean"
   "living in Toronto"

   "clojure programmer"
   "it's been a long journey to get here, and it's currently my favorite language"})

(defn about-page
  []
  [:div
   [:ul
    (for [[key val] intro-map]
      ^{:key key}
      [:li.about-list key
       [:p.value val]])]])
