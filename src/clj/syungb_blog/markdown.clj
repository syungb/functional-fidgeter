(ns syungb-blog.markdown
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [markdown.core :as md]))

(def folder-path "resources/public/mds/")

(def read-files
  (let [list-of-mds (.list (io/file folder-path))]
    (for [md list-of-mds]
      (let [idx           (first (clojure.string/split md #"-"))
            md-read       (slurp (str folder-path md))
            {:keys [metadata html]} (md/md-to-html-string-with-meta md-read)]
        {:id       idx
         :metadata (assoc metadata :id idx)
         :html     html}))))
;; returns {:metadata .. :html .. :id "001"}

(defn read-file [id]
  (-> (filter #(= id (:id %)) read-files)
      first
      json/generate-string))

(def read-files-to-routers
  (let [list-of-mds (.list (io/file folder-path))]
    (doall
     (for [md list-of-mds]
       (let [idx           (first (clojure.string/split md #"-"))
             md-read       (slurp (str folder-path md))
             {:keys [metadata html]} (md/md-to-html-string-with-meta md-read)]
         [(str "/blog-contents/" idx)
          {:get {:summary (str "blog post - " idx)
                 :handler (fn [_]
                            {:status 200
                             :body   (-> {:metadata metadata
                                          :html     html}
                                         json/generate-string)})}}])))))
