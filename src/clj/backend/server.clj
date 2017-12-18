(ns backend.server
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [common.localization :refer [tr]]
            [backend.routes :as routes]
            [hiccup.core :as hiccup]
            [hiccup.page :as page]
            [integrant.core :as ig]
            [immutant.web :as immutant]
            [ring.util.response :as response]
            [ring.middleware.content-type :as content-type]
            [eines.core :as eines]
            [eines.server.immutant :as eines-immutant]
            [eines.middleware.rsvp :as rsvp]
            [eines.middleware.session :as session])
  (:import (org.apache.commons.codec.digest DigestUtils)))

(defmethod ig/init-key :adapter/immutant [_ {:keys [handler] :as opts}]
  (immutant/run handler (-> opts (dissoc :handler))))

(defmethod ig/halt-key! :adapter/immutant [_ server]
  (immutant/stop server))

;;
;; Index page
;;

(defn index []
  (hiccup/html
   (page/html5
    [:head
     [:title (tr :page-title)]
     [:meta {:charset "utf-8"}]
     [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
     (page/include-css "/css/style.css")]
    [:body
     [:div#app
      [:div.loading
       [:h1 (tr :loading)]]]
     [:div#dev]
     (page/include-js "/js/main.js")])))

(defn create-index-handler []
  (fn [req]
    (if (= (:request-method req) :get)
      (-> (index)
          response/response
          (response/content-type "text/html; charset=utf-8")))))

(defn create-static-handler []
  ;; Serve files from filesystem during development
  ;; and from classpath in uberjar
  (-> (if (.exists (io/file "target/dev/resources/"))
        (fn [req]
          (if (= (:request-method req) :get)
            (response/file-response (:uri req) {:root "target/dev/resources/"})))
        (fn [req]
          (if (= (:request-method req) :get)
            (response/resource-response (:uri req)))))
      (content-type/wrap-content-type)))

(defmethod ig/init-key :handler/ring [_ _]
  (some-fn (-> (eines/handler-context routes/route-message
                                      {:middlewares [(rsvp/rsvp-middleware)]})
               (eines-immutant/create-handler))
           (create-static-handler)
           (create-index-handler)))
