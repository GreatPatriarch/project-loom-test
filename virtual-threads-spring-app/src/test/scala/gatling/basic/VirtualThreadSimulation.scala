package gatling.basic

import com.dnu.VirtualThreadsSpringAppApplication
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

import scala.concurrent.duration._
import org.springframework.boot.SpringApplication

import scala.language.postfixOps

class VirtualThreadSimulation extends Simulation {

  before {
    val app = SpringApplication.run(classOf[VirtualThreadsSpringAppApplication])
    app.registerShutdownHook()
  }


  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  def randomString(length: Int) = {
    val r = new scala.util.Random
    val aToZ = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    (1 to length).map(_ => aToZ(r.nextInt(aToZ.length))).mkString
  }

  val firstNameFeeder = Iterator.continually(Map("firstName" -> ("TestUser" + scala.util.Random.nextInt(100000))))
  val lastNameFeeder = Iterator.continually(Map("lastName" -> ("TestLastName" + scala.util.Random.nextInt(100000))))
  val emailFeeder = Iterator.continually(Map("email" -> ("testEmail" + randomString(5) + "@example.com")))

  val titleFeeder = Iterator.continually(Map("title" -> ("Note Title " + scala.util.Random.nextInt(100000))))
  val contentFeeder = Iterator.continually(Map("content" -> ("Note Content " + randomString(50))))

  val userAndNoteCreationScenario = scenario("Create users and add notes")
    .repeat(50) {
      feed(firstNameFeeder)
        .feed(lastNameFeeder)
        .feed(emailFeeder)
        .exec(http("POST User")
          .post("/api/v1/users/user-create")
          .body(StringBody("""{"firstName": "${firstName}","lastName": "${lastName}","email": "${email}"}""")).asJson
          .check(status.is(200), jsonPath("$.id").ofType[Long].saveAs("userId"))
        )
        .exec{session => println("User ID : " + session("userId").as[String]); session}
        .pause(1)
        // Create 5 notes for each user
        .foreach((0 until 5).toList, "i") {
          feed(titleFeeder)
            .feed(contentFeeder)
            .exec(http("POST Note for User ${userId}")
              .post("/api/v1/notes/note-create/${userId}")
              .body(StringBody("""
            {
              "title": "${title}",
              "content": "${content}"
            }
            """)).asJson
              .check(status.is(201)).check(bodyString.saveAs("responseBody"))
            )
            .exec{session => println(session("responseBody").as[String]); session}
        }
    }

  setUp(
    userAndNoteCreationScenario.inject(rampUsers(300) during (60 seconds))
  ).protocols(httpProtocol)
}