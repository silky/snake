(ns snake.core
  (:require [snake.react :as r]
            [snake.directions :as d]))

(defn get-state [component]
  (-> component .-state .-wrapper))

(defn log [value]
  (js/console.log (clj->js value))
  value)

(defn explode-coord [[x y]]
  [(* 10 x) (* 10 y)])

(defn snake-segment [[[x1 y1] [x2 y2]]]
  (r/line {:x1 x1 :y1 y1 
           :x2 x2 :y2 y2
           :style {:stroke "rgb(255,0,0)"
                   :strokeWidth 2}}))

(defn history->lines [{:keys [history length]}]
  (->> (take length history)
       (map explode-coord)
       (partition 2 1)
       (map snake-segment)))

(def component
  (r/create-class
    {:getInitialState
     (fn []
       #js {:wrapper
            {:history '([1 3] [1 2] [1 1])
             :length 3
             :direction d/south
             :food [2 2]}})
     :render
     (fn []
       (this-as this
                (r/svg {} (history->lines (get-state this)))))}))

(defn advance-snake [component]
  (let [old-state (get-state component)
        direction (:direction old-state)
        new-state (update-in old-state [:history] #(conj % (direction (first %))))]
    (.setState component #js {:wrapper new-state})))

(let [new-game (component #js {})]
  (js/setInterval (partial advance-snake new-game) 500)
  (r/render-component new-game (js/document.getElementById "content")))

