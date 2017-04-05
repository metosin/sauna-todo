(ns intro.basics)

;;
;; So what's the syntax of Clojure like?
;;

;; This is a comment.
; This is also a comment.
;So is this.

;; Numbers.

1
2.5
2/3


;; Booleans.

true
false


;; Strings.

"Hello World"
(type "Hello World")


;; Keywords.

:hello
:world


;; nil.

nil


;; Vectors.

[1 2 3 4]

;; Maps.

{:nimi "Matti"
 :favorite-food "pizza"}


;; Lists.

'(1 2 3 4 5 6 "List elements don't have to be of the same type")

;; What's the quote before the parentheses in lists?
;; Lists evaluate into function calls if they are not quoted.
;; Code is data! Lisp programs manipulate the same data structures they created
;; with.


;; Calling functions.

(+ 1 1)

(* 10 (+ 1 1))

(str 3 " omenaa")

(/ 10 5)


;; Your own functions. How to calculate average of two numbers?

(defn my-avg
  [x y]
  (/ (+ x y) 2))

(my-avg 2 4)


;; Anonymous functions.

(fn [x y]
  (/ (+ x y) 2))

#(/ (+ %1 %2) 2)


;; Function for checking is String is blank?
;; Compare this to Apache Commons StringUtils (Java)

(defn blank?
  [s]
  (every? (fn [c] (Character/isWhitespace c)) s))

(blank? nil)
(blank? "")
(blank? "   \t \n   ")
(blank? "    a   ")


;; Some useful sequence functions (count conj, first, second nth).
;; There are many more.

(count [1 2 3])
(conj [1 2 3 4] 5)
(first [1 2 3])
(second [1 2 3])
(nth [1 2 3 4 5] 1)


;; Some useful map functions (keys, vals, get, assoc, dissoc, merge)

(keys {:a 1 :b 2})
(vals {:a 1 :b 2})
(get {:a 1 :b 2} :b)
(assoc {:a 1 :b 2} :c 3)
(dissoc {:a 1 :b 2} :b)
(merge {:a 1} {:b 2})


;; Are there any variables? Or constants?

(def my-name "Matti")

my-name


;; if
(if true
  "Yes"
  "No")


;; let

(let [x 1
      y 2]
  (+ x y))

(let [sum (+ 2 4)]
  (/ sum 2))


;; Java interop

;; As Java:  "hello world".toUpperCase();
(.toUpperCase "hello world")


;;
;; Things you may need exploring this codebase!
;;

;; Destructing function argument lists and lets

(let [[a b] [1 2]]
  (+ a b))

(let [{:keys [a b] :as m} {:a 1 :b 2}]
  (+ a b))


;; Atoms (creating an atom, dereffing it, changing it's value)

(def some-name (atom "Matti"))

some-name

@some-name

(reset! some-name "Eemeli")

@some-name

(swap! some-name clojure.string/upper-case)

@some-name
