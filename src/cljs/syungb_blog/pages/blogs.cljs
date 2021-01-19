(ns syungb-blog.pages.blogs
  (:require-macros
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [reagent.core :as r]
    [reagent.session :as session]))

(defonce blog-list (atom nil))
;(defonce blog (atom nil))

(defn- parse-json [string]
  (.parse js/JSON string))

(defn- json->map [json]
  (js->clj json :keywordize-keys true))

(defn- get-req [at & id]
  (let [url (if-not id "/blog-contents" (str "/blog-contents/" (first id)))]
    (go
      (let [{body :body} (<! (http/get url))
            json (parse-json body)]
        (reset! at json)))))

(defn blog-list-page []
  (let [_ (get-req blog-list)]
    [:ul.blog-lists
     (for [list @blog-list
           :let [{:keys [id date title summary keywords]} (json->map list)]]
       [:li.blog-lists__list
        {:key id}
        [:a.blog-lists__title
         {:href (str "/blogs/" id)}
         title]
        [:span.blog-lists__date date]
        [:span.blog-lists__summary summary]])]))

(defn blog-page []
  (let [blog         (r/atom nil)
        routing-data (session/get :route)
        blog-id      (get-in routing-data [:route-params :blog-id])
        _            (get-req blog blog-id)]
    (fn []
      (let [{:keys [metadata html]} (json->map @blog)
            {:keys [title date keywords]} metadata]
       [:div.blog-page
        [:div
         [:a {:href "/blogs"} "< Back to list"]]
        [:div.blog-area
         [:div.blog-area__header
          [:h3 title]
          [:h4 date]]
         [:div
          {:dangerouslySetInnerHTML {:__html html}}]]]))))