(ns clojurebot.hudson
  (:require [clj-http.client :as http]))

(defn build? [{:keys [message]}]
  (and message
       (re-find #"^build" message)))

(defn do-a-build [{:keys [message config] :as a-map}]
  (let [hudson (:hudson config)
        hudson (if (.endsWith hudson "/") hudson (str hudson "/"))
        [build & opts] (.split (last (re-find #"^(build) (.*)" message)) " ")
        build-url (if (seq opts)
                    (format "%sjob/%s/buildWithParameters" hudson build)
                    (format "%sjob/%s/build" hudson build))]
    (http/get build-url {:basic-auth (:hudson-credentials config)
                         :query-params (apply hash-map opts)})
    (format "%s/job/%s" hudson build)))
