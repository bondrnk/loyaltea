package loyaltea
package repo

class CampaignRepo {

}

object CampaignRepo {
  case class Dummy() extends CampaignRepo
  case class Postgres() extends CampaignRepo
}
