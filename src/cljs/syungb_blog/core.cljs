(ns syungb-blog.core
  (:require
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rdom]
    [reagent.session :as session]
    [reitit.frontend :as reitit]
    [syungb-blog.pages.about :refer [about-page]]
    [syungb-blog.pages.blogs :refer [blog-list-page blog-page]]
    [syungb-blog.pages.home :refer [home-page]]
    [clerk.core :as clerk]
    [accountant.core :as accountant]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/about" :about]
    ["/blogs"
     ["" :blog-contents]
     ["/:blog-id" :blog]]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components

(defn menu []
  [:div.menu-wrapper
   [:img.menu-logo
    {:src "images/ff_logo.svg"
     :alt "my_logo"}]
   [:div.menu-and-description
    [:ul.menu__list
     [:li.menu-item
      [:a
       {:href (path-for :index)} "
      home"]]
     [:li.menu-item
      [:a
       {:href (path-for :about)}
       "about"]]
     [:li.menu-item
      [:a
       {:href (path-for :blog-contents)}
       "blog"]]]
    [:div.menu-page-description
     [:p "COZY SPACE TO ENUMERATE PROS OF BEING A FUNCTIONAL FIDGETER"]]]])


;; -------------------------
;; Translate routes -> page components

(defn page-for [route]
  (case route
    :index #'home-page
    :about #'about-page
    :blog-contents #'blog-list-page
    :blog #'blog-page))


;; -------------------------
;; Page mounting component

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [menu]
       [:div.main
        [page]]
       [:footer
        [:p "Written by Fidgeter while fidgeting.."]]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (let [match        (reitit/match-by-path router path)
             current-page (:name (:data match))
             route-params (:path-params match)]
         (reagent/after-render clerk/after-render!)
         (session/put! :route {:current-page (page-for current-page)
                               :route-params route-params})
         (clerk/navigate-page! path)))

     :path-exists?
     (fn [path]
       (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
