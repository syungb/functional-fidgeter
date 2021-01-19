(ns syungb-blog.pages.blogs
  (:require-macros
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [reagent.core :as r]
    [reagent.session :as session]))

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
  (let [blog-list (r/atom nil)]
    (r/create-class
      {:component-did-mount
       (fn [_]
         (get-req blog-list))

       :reagent-render
       (fn []
         [:ul.blog-lists
          (for [list @blog-list
                :let [{:keys [id date title summary keywords]} (json->map list)]]
            [:li.blog-lists__list
             {:key id}
             [:div.blog-lists__title
              [:a
               {:href (str "/blogs/" id)}
               title]]
             [:span.blog-lists__date date]
             [:span.blog-lists__summary summary]])])})))

(defn blog-page []
  (let [blog         (r/atom nil)
        routing-data (session/get :route)
        blog-id      (get-in routing-data [:route-params :blog-id])]
    (r/create-class
      {:component-did-mount
       (fn [_]
         (get-req blog blog-id))
       :reagent-render
       (fn []
         (let [{:keys [metadata html]} (json->map @blog)
               {:keys [title date keywords]} metadata]
          [:div.blog-page
           [:div
            [:a {:href "/blogs"} "< Back to list"]]
           [:div.blog-page
            [:div.blog-page-header
             [:h3.blog-page-header__title title]
             [:p.blog-page-header__date date]]
            [:div.blog-page-body
             {:dangerouslySetInnerHTML {:__html html}}]]]))})))