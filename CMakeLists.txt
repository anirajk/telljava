project(telljava)
cmake_minimum_required(VERSION 3.0)

# Set compile options
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall")

find_package(Java REQUIRED)

macro(javah JAVA_TARGET)
    add_custom_target(javah_${JAVA_TARGET} DEPENDS ${JAVA_TARGET}
        COMMAND "${Java_JAVAH_EXECUTABLE}" -jni -d "${CMAKE_CURRENT_BINARY_DIR}" ${ARGN}
        WORKING_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/${CMAKE_JAVA_TARGET_OUTPUT_DIR}${CMAKE_FILES_DIRECTORY}/${JAVA_TARGET}.dir"
        COMMENT "Updating JNI headers for ${JAVA_TARGET}" VERBATIM)
endmacro(javah)

include(UseJava)
set(CMAKE_JAVA_COMPILE_FLAGS "-source" "1.8" "-target" "1.8" "-XDignore.symbol.file")
set(CMAKE_JNI_TARGET TRUE)
add_jar(telljava
    java/src/main/ch/ethz/tell/ClientManager.java
    java/src/main/ch/ethz/tell/Schema.java
    java/src/main/ch/ethz/tell/Transaction.java
    java/src/main/ch/ethz/tell/PredicateType.java
    java/src/main/ch/ethz/tell/ScanQuery.java
    java/src/main/ch/ethz/tell/ScanIterator.java
    java/src/main/ch/ethz/tell/Pair.java
    java/src/main/ch/ethz/tell/Unsafe.java
    java/src/main/ch/ethz/tell/UnsafeException.java
    java/src/test/ch/ethz/tell/TestRunner.java
    VERSION 1.0)
javah(telljava
    "ch.ethz.tell.ClientManager"
    "ch.ethz.tell.Schema"
    "ch.ethz.tell.Transaction"
    "ch.ethz.tell.ScanIterator")

find_package(JNI REQUIRED)
find_package(TellStore REQUIRED)
find_package(Crossbow REQUIRED)

set(impl_SRCS
        schema.cpp
        clientManager.cpp
        transaction.cpp)

add_library(telljavaimpl SHARED ${impl_SRCS})
add_dependencies(telljavaimpl javah_telljava)
target_link_libraries(telljavaimpl PRIVATE ${JNI_LIBRARIES})
target_link_libraries(telljavaimpl PRIVATE tellstore-client)
target_include_directories(telljavaimpl PRIVATE ${JNI_INCLUDE_DIRS})
target_include_directories(telljavaimpl PRIVATE ${CMAKE_CURRENT_BINARY_DIR})
target_include_directories(telljavaimpl PRIVATE ${Crossbow_INCLUDE_DIRS})
