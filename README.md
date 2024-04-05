# COP4520-Assignment-3
## Part 1: Birthday Presents
In this I use a synchronized linked list to implement the chain of presents and an pre-populated arraylist to act as the bag of unordered gifts. Even though the bag is technically ordered the servants will take a gift out of the bag at random with respect to the gift's index in the list, thus simulating an unordered bag. When the servants take a gift from the bag they add the gift to the chain and then sort the chain before releasing the lock to the chain so that the gift is in the correct position. The servants then take a random gift out of the chain and write a thank you note, increasing the thank you counter. The servants can also randomly check if a certain gift is in the chain at the current moment. Once the bag and chain are both empty, then it must mean that all the gifts have been placed once in the chain then had a thank you note written, then removed from the chain. If worked correctly the output in console will show the chain and the bag as empty and the thank you counter at exactly 500,000.
### Compile and run:
```java birthdayGifts.java```
