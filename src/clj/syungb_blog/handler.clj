(ns syungb-blog.handler
  (:require
   [cheshire.core :as json]
   [reitit.ring :as reitit-ring]
   [syungb-blog.markdown :refer [read-files read-files-to-routers]]
   [syungb-blog.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]))

(def mount-target
  [:div#app
   [:h2 "Welcome to syungb-blog"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")
    [:script "syungb_blog.core.init_BANG_()"]]))

(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(def router
  (reitit-ring/router
   [["/" {:get {:handler index-handler}}]
    ["/about" {:get {:handler index-handler}}]
    ["/blog-contents"
     {:get {:summary "list of blog contents' metadata"
            :handler (fn [_]
                       {:stats 200
                        :body  (-> (map :metadata read-files)
                                   json/generate-string)})}}]
    read-files-to-routers]))

(def app
  (reitit-ring/ring-handler
   router
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
