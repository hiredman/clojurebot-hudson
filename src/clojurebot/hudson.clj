(ns clojurebot.hudson
  (:require [clj-http.client :as http]))

(defn build? [{:keys [message]}]
  (and message
       (re-find #"^build" message)))

(defn do-a-build [{:keys [message config] :as a-map}]
  (let [hudson (:hudson config)
        hudson (if (.endsWith hudson "/") hudson (str hudson "/"))
        build-url (format "%sjob/%s/build?delay=0sec"
                          hudson
                          (last (re-find #"^(build) (.*)" message)))]
    (:status
     (http/get build-url
               {:basic-auth (:hudson-credentials config)}))))
