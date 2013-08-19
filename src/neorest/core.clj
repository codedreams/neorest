(ns neorest.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [environ.core :refer :all]))

(defn query [cypher params-map]
  (let [href (str (env :neohost) "cypher")
        body (json/generate-string {:query cypher :params params-map})
        {:keys [status body] :as req} (client/post href
                                                   {:body body
                                                    :accept :json
                                                    :content-type :json})]
    (assoc (json/parse-string body true) :status status)))

(defn create-index [params-map]
  (let [href (str (env :neohost) "index/node/")]
    (client/post href
                 {:body (json/generate-string params-map)
                  :accept :json
                  :content-type :json})))

(defn delete-index [idx-name]
  (client/delete (str (env :neohost) "index/node/" idx-name)))

(defn add-to-index [idx-name self k v]
  (let [href (str (env :neohost) "index/node/" idx-name)
        body (json/generate-string {:key k :value v :uri self})]
    (client/post href {:body body
                       :accept :json
                       :content-type :json})))

(defn rm-from-index [idx-name id-or-more]
  (client/delete (str (env :neohost) "index/node/" idx-name "/" id-or-more)))

(defn created? [result]
  (= (:status result) 201))


